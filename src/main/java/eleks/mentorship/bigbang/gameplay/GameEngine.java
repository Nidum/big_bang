package eleks.mentorship.bigbang.gameplay;

import eleks.mentorship.bigbang.domain.Position;
import eleks.mentorship.bigbang.exception.UserMissingException;
import eleks.mentorship.bigbang.mapper.JsonMessageMapper;
import eleks.mentorship.bigbang.websocket.MessageAggregator;
import eleks.mentorship.bigbang.websocket.WebSocketMessageSubscriber;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.MessageType;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    private GameState currentGameState;
    private Map<PlayerInfo, Boolean> playerReady;

    // TODO: inject game field (randomly).
    public GameEngine(JsonMessageMapper mapper, MessageAggregator aggregator) {
        this.mapper = mapper;
        this.currentGameState = new GameState(new HashSet<>(), new GameField("gamefield"));
        this.messageSubscriber = new WebSocketMessageSubscriber();
        this.aggregator = aggregator;
        this.playerReady = new HashMap<>();
    }

    public void buildGamePlay() {
        Flux<GameMessage> cache = messageSubscriber.getOutputEvents()
                .cache(0);

        Mono<GameMessage> startGameCounterMono = Mono.just(new StartCounterMessage());

        Flux<GameMessage> gameStartFlux = Flux
                .just(new GameStartMessage(), currentGameState)
                .delaySubscription(Duration.ofSeconds(3));

        Predicate<GameMessage> isGameMessage = message -> {
            MessageType msgType = message.getType();
            return !(msgType.equals(START_COUNTER)
                    && !(msgType.equals(BOMB))
                    && !(msgType.equals(MOVE))
                    && !(msgType.equals(READY)));
        };

        Flux<GameMessage> gameMessageFlux = cache
                .filter(isGameMessage)
                .doOnNext(msg -> {
                    if (msg.getType().equals(EXPLOSION)) {
                        onBombExplosion((BombExplosionMessage) msg);
                    }
                });

        Flux<GameMessage> gamePlayMessageFlux = cache
                .filter(IS_POSITIONING_MESSAGE)
                .map(x -> (PositioningMessage) x)
                .buffer(Duration.ofSeconds(BUFFER_WINDOW))
                .flatMap(messages ->
                        aggregator.aggregate(messages, currentGameState));

        messageSubscriber.setOutputEvents(
                cache.filter(x -> x instanceof ReadyMessage || x instanceof RoomStateMessage)
                        .map(this::processReadyMessages)
                        .takeWhile(x -> playerReady.size() != MAX_CONNECTIONS || playerReady.values().contains(false))
                        .concatWith(startGameCounterMono
                                .concatWith(gameStartFlux)
                                .concatWith(gameMessageFlux
                                        .mergeWith(gamePlayMessageFlux)
                                )
                        )
        );
        ; // TODO: ignore messages from dead players.
    }

    private void onBombExplosion(BombExplosionMessage explosionMessage) {
        GamePlayer gamePlayer = explosionMessage.getOwner();
        GamePlayer fieldPlayer = currentGameState.getPlayers().stream()
                .filter(x -> x.getPlayerInfo().getUserId().equals(gamePlayer.getPlayerInfo().getUserId()))
                .findFirst()
                .orElseThrow(UserMissingException::new);
        fieldPlayer.setBombsLeft(fieldPlayer.getBombsLeft() + 1);
        GameField gameField = currentGameState.getGameField();
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

        List<GamePlayer> damagedPlayers = currentGameState.getPlayers().stream()
                .filter(player -> {
                    Position position = player.getPosition();
                    return (position.getX() >= leftX && position.getX() <= rightX) ||
                            (position.getY() >= leftY && position.getY() <= rightY);
                })
                .collect(Collectors.toList());
        gameField.getBombs().get(bombPosition.getX()).set(bombPosition.getY(), false);
        currentGameState.getPlayers().stream().filter(damagedPlayers::contains).forEach(
                p -> {
                    p.setLivesLeft(p.getLivesLeft() - 1);
                }
        );
        explosionMessage.setDamaged(damagedPlayers);
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
                .doOnNext(x -> registerPlayer(x.getPlayerInfo()))
                .map(x -> new RoomStateMessage(convert(playerReady)))
                ;
    }

    private void registerPlayer(PlayerInfo playerInfo) {
        GamePlayer gamePlayer = new GamePlayer(playerInfo, currentGameState.getFreeSpawn());
        gamePlayer.setLastMoveTime(Instant.now());
        currentGameState.getPlayers().add(gamePlayer);
        playerReady.put(playerInfo, false);
    }

    public Flux<GameMessage> getGameFlow() {
        return messageSubscriber
                .getOutputEvents();
    }

}
