package eleks.mentorship.bigbang.gameplay;

import eleks.mentorship.bigbang.domain.Position;
import eleks.mentorship.bigbang.exception.UserMissingException;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eleks.mentorship.bigbang.serialiazation.PlayerReadyConverter.convert;
import static eleks.mentorship.bigbang.websocket.Room.MAX_CONNECTIONS;
import static eleks.mentorship.bigbang.websocket.message.MessageType.*;
import static eleks.mentorship.bigbang.websocket.message.MessageUtils.IS_POSITIONING_MESSAGE;

@Data
@AllArgsConstructor
public class GameEngine {
    private static final int EXPLOSION_DELAY = 5; // In seconds.
    private static final int EXPLOSION_RADIUS = 3; // In cells.
    private static final int BUFFER_WINDOW = 2000; // In milliseconds.

    private final JsonMessageMapper mapper;
    private final WebSocketMessageSubscriber messageSubscriber;
    private final MessageAggregator aggregator;

    private Map<PlayerInfo, Boolean> playerReady;
    private Flux<GameState> state;

    // TODO: inject game field (randomly).
    public GameEngine(JsonMessageMapper mapper, MessageAggregator aggregator) {
        this.mapper = mapper;
        this.messageSubscriber = new WebSocketMessageSubscriber();
        this.aggregator = aggregator;
        this.playerReady = new HashMap<>();
        buildGamePlay();
    }

    public void buildGamePlay() {
        Flux<GameMessage> cache = messageSubscriber.getOutputEvents()
                .cache(0);

        Mono<GameMessage> startGameCounterMono = Mono.just(new StartCounterMessage());

        Flux<GameState> gameStateFlux = Flux
                .just(new GameState(new HashSet<>(), new GameField("gamefield")));

        Flux<GameMessage> gameStartFlux = Mono
                .just((GameMessage) new GameStartMessage())
                .concatWith(gameStateFlux)
                .delaySubscription(Duration.ofSeconds(3));

        Flux<GameMessage> gamePlayMessageFlux = cache
                .filter(IS_POSITIONING_MESSAGE)
                .map(x -> (PositioningMessage) x)
                .buffer(Duration.ofMillis(BUFFER_WINDOW))
                .withLatestFrom(gameStateFlux, aggregator::aggregate)
                .flatMap(x -> x)
                .map(msg -> {
                    if (msg.getType().equals(EXPLOSION)) {
                        return onBombExplosion((BombExplosionMessage) msg);
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
        this.state = gameStateFlux.cache();
    }

    private GameState onBombExplosion(BombExplosionMessage explosionMessage) {
        GamePlayer bombOwner = explosionMessage.getOwner();
        GamePlayer fieldPlayer = explosionMessage.getPlayers().stream()
                .filter(x -> x.getPlayerInfo().getUserId().equals(bombOwner.getPlayerInfo().getUserId()))
                .findFirst()
                .orElseThrow(UserMissingException::new);

        explosionMessage.getPlayers().remove(fieldPlayer);
        explosionMessage.getPlayers().add(new GamePlayer(fieldPlayer.getPlayerInfo(),
                fieldPlayer.getLivesLeft(),
                fieldPlayer.getBombsLeft() + 1,
                fieldPlayer.getPosition(),
                fieldPlayer.getLastMoveTime()));

        GameField gameField = explosionMessage.getGameField();
        int width = gameField.getWidth();
        int height = gameField.getHeight();

        Position bombPosition = explosionMessage.getPosition();

        int leftX = getRange(bombPosition.getX(),
                (i) -> (i > 0 && i > bombPosition.getX() - EXPLOSION_RADIUS),
                gameField, true, -1);
        int rightX = getRange(bombPosition.getX(),
                (i) -> (i < width && i < bombPosition.getX() + EXPLOSION_RADIUS),
                gameField, true, 1);
        int leftY = getRange(bombPosition.getY(),
                (i) -> (i > 0 && i > bombPosition.getY() - EXPLOSION_RADIUS),
                gameField, false, -1);
        int rightY = getRange(bombPosition.getY(),
                (i) -> (i < height && i < bombPosition.getY() + EXPLOSION_RADIUS),
                gameField, false, 1);

        List<GamePlayer> damagedPlayers = explosionMessage.getPlayers().stream()
                .filter(player -> {
                    Position position = player.getPosition();
                    return (position.getX() >= leftX && position.getX() <= rightX) ||
                            (position.getY() >= leftY && position.getY() <= rightY);
                })
                .collect(Collectors.toList());
        gameField.getBombs().get(bombPosition.getX()).set(bombPosition.getY(), false);

        List<GamePlayer> nonDamagedPlayers = explosionMessage
                .getPlayers()
                .stream()
                .filter(player -> !damagedPlayers.contains(player))
                .collect(Collectors.toList());

        List<GamePlayer> updateDamagedPlayers = explosionMessage.getPlayers()
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

    private int getRange(int position, Predicate<Integer> range, GameField gameField, boolean isHorizontal, int step) {
        int res = position;

        for (int i = position; range.test(i); i += step) {
            GameFieldCell gameFieldCell;
            if (isHorizontal) {
                gameFieldCell = gameField.getGameField().get(i).get(position);
            } else {
                gameFieldCell = gameField.getGameField().get(position).get(i);
            }
            if (!gameFieldCell.equals(GameFieldCell.FIELD)) {
                res = i;
                break;
            }
        }
        return res;
    }

    public Flux<GameMessage> subscribePlayer(Flux<UserMessage> messageFluxCache) {
        return messageFluxCache
                .filter(x -> x.getType().equals(CONNECT))
                .take(1)
                .withLatestFrom(state, (msg, currentGameState) -> {
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
