package eleks.mentorship.bigbang.gameplay;

import eleks.mentorship.bigbang.domain.Position;
import eleks.mentorship.bigbang.exception.UserMissingException;
import eleks.mentorship.bigbang.gameplay.field.ExplosionRange;
import eleks.mentorship.bigbang.gameplay.field.GameField;
import eleks.mentorship.bigbang.mapper.JsonMessageMapper;
import eleks.mentorship.bigbang.websocket.MessageAggregator;
import eleks.mentorship.bigbang.websocket.WebSocketMessageSubscriber;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.server.*;
import eleks.mentorship.bigbang.websocket.message.user.PositioningMessage;
import eleks.mentorship.bigbang.websocket.message.user.ReadyMessage;
import eleks.mentorship.bigbang.websocket.message.user.UserMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eleks.mentorship.bigbang.gameplay.field.ExplosionRange.getExplosionRange;
import static eleks.mentorship.bigbang.serialiazation.PlayerReadyConverter.convert;
import static eleks.mentorship.bigbang.websocket.Room.MAX_CONNECTIONS;
import static eleks.mentorship.bigbang.websocket.message.MessageType.*;
import static eleks.mentorship.bigbang.websocket.message.MessageUtils.IS_POSITIONING_MESSAGE;

@Data
@AllArgsConstructor
public class GameEngine {
    private static final int EXPLOSION_DELAY = 2; // In seconds.
    private static final int EXPLOSION_RADIUS = 3; // In cells.
    private static final int BUFFER_WINDOW = 200; // In milliseconds.

    private final JsonMessageMapper mapper;
    private final WebSocketMessageSubscriber messageSubscriber;
    private final MessageAggregator aggregator;

    private DirectProcessor<GameState> stateConsumer;
    private Flux<GameState> stateProducer;


    private Map<PlayerInfo, Boolean> playerReady;

    // TODO: inject game field (randomly).
    public GameEngine(JsonMessageMapper mapper, MessageAggregator aggregator) {
        this.mapper = mapper;
        this.messageSubscriber = new WebSocketMessageSubscriber();
        this.aggregator = aggregator;
        this.playerReady = new HashMap<>();
        this.stateConsumer = DirectProcessor.create();
        this.stateProducer = stateConsumer
                .startWith(new GameState(new HashSet<>(), new GameField("gamefield")))
                .cache();

        buildGamePlay();
    }

    public void buildGamePlay() {
        Flux<GameMessage> cache = messageSubscriber.getOutputEvents()
                .cache(0);

        Mono<GameMessage> startGameCounterMono = Mono.just(new StartCounterMessage());
        Flux<GameMessage> gameStartFlux = Mono
                .just((GameMessage) new GameStartMessage())
                .mergeWith(stateProducer.take(1))
                .delaySubscription(Duration.ofSeconds(3));

        Flux<GameMessage> gamePlayMessageFlux = cache
                .filter(IS_POSITIONING_MESSAGE)
                .map(x -> (PositioningMessage) x)
                .buffer(Duration.ofMillis(BUFFER_WINDOW))
                .withLatestFrom(stateProducer, aggregator::aggregate)
                .flatMap(x -> x)
                .doOnNext(msg -> {
                    if (msg.getType().equals(STATE)) {
                        System.out.println("Got stateConsumer change: " + msg);
                        stateConsumer.onNext((GameState) msg);
                    }
                })
                .withLatestFrom(stateProducer, (msg, state) -> {
                    if (msg.getType().equals(EXPLOSION)) {
                        GameState gameState = onBombExplosion((BombExplosionMessage) msg, state);
                        stateConsumer.onNext(gameState);
                        return gameState;
                    }
                    return msg;
                });

        messageSubscriber.setOutputEvents(
                cache.filter(x -> x instanceof ReadyMessage || x instanceof RoomStateMessage)
                        .map(this::processReadyMessages)
                        .takeWhile(x -> playerReady.size() != MAX_CONNECTIONS || playerReady.values().contains(false))
                        .concatWith(startGameCounterMono
                                .concatWith(gameStartFlux)
                                .concatWith(gamePlayMessageFlux)
                        )
        );
        ; // TODO: ignore messages from dead players.
    }

    private GameState onBombExplosion(BombExplosionMessage explosionMessage, GameState oldState) {
        GamePlayer bombOwner = explosionMessage.getOwner();
        GamePlayer fieldPlayer = oldState
                .getPlayers()
                .stream()
                .filter(x -> x.getPlayerInfo().getUserId().equals(bombOwner.getPlayerInfo().getUserId()))
                .findFirst()
                .orElseThrow(UserMissingException::new);

        oldState.getPlayers().remove(fieldPlayer);
        oldState.getPlayers().add(new GamePlayer(bombOwner.getPlayerInfo(),
                fieldPlayer.getLivesLeft(),
                bombOwner.getBombsLeft() + 1,
                fieldPlayer.getPosition(),
                bombOwner.getLastMoveTime()));

        GameField gameField = oldState.getGameField();
        Position bombPosition = explosionMessage.getPosition();

        ExplosionRange explosionRange = getExplosionRange(gameField, bombPosition, EXPLOSION_RADIUS);
        List<GamePlayer> damagedPlayers = oldState.getPlayers().stream()
                .filter(player -> explosionRange.isInRange(player.getPosition()))
                .collect(Collectors.toList());
        gameField.getBombs().get(bombPosition.getY()).set(bombPosition.getX(), false);

        List<GamePlayer> nonDamagedPlayers = oldState
                .getPlayers()
                .stream()
                .filter(player -> !damagedPlayers.contains(player))
                .collect(Collectors.toList());

        List<GamePlayer> updateDamagedPlayers = oldState
                .getPlayers()
                .stream()
                .filter(damagedPlayers::contains)
                .map(player -> new GamePlayer(
                        player.getPlayerInfo(),
                        player.getLivesLeft() - 1,
                        player.getBombsLeft(),
                        player.getPosition(),
                        player.getLastMoveTime()))
                .collect(Collectors.toList());

        explosionMessage.setDamaged(damagedPlayers);

        Set<GamePlayer> gamePlayers = Stream.concat(nonDamagedPlayers.stream(), updateDamagedPlayers.stream())
                .collect(Collectors.toSet());
        return new GameState(gamePlayers, gameField);
    }

    private GameMessage processReadyMessages(GameMessage msg) {
        if (msg.getType().equals(READY)) {
            playerReady.put(((ReadyMessage) msg).getPlayerInfo(), true);

            if (playerReady.size() == MAX_CONNECTIONS && !playerReady.values().contains(false)) {
                return new StartCounterMessage();
            } else {
                return new RoomStateMessage(convert(playerReady));
            }
        }
        return msg;
    }

    public Flux<GameMessage> subscribePlayer(Flux<UserMessage> messageFluxCache) {
        return messageFluxCache
                .filter(x -> x.getType().equals(CONNECT))
                .take(1)
                .withLatestFrom(stateProducer, (msg, currentGameState) -> {
                    registerPlayer(msg.getPlayerInfo(), currentGameState);
                    return msg;
                })
                .map(x -> new RoomStateMessage(convert(playerReady)))
                ;
    }

    private void registerPlayer(PlayerInfo playerInfo, GameState currentGameState) {
        GamePlayer gamePlayer = new GamePlayer(playerInfo, currentGameState.getFreeSpawn(), Instant.now());
        currentGameState.getPlayers().add(gamePlayer);
        playerReady.put(playerInfo, false);
    }

    public Flux<GameMessage> getGameFlow() {
        return messageSubscriber
                .getOutputEvents();
    }

}
