package eleks.mentorship.bigbang.gameplay;

import eleks.mentorship.bigbang.Player;
import eleks.mentorship.bigbang.common.exception.UserMissingException;
import eleks.mentorship.bigbang.mapper.JsonMessageMapper;
import eleks.mentorship.bigbang.util.Position;
import eleks.mentorship.bigbang.websocket.MessageAggregator;
import eleks.mentorship.bigbang.websocket.WebSocketMessageSubscriber;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.server.*;
import eleks.mentorship.bigbang.websocket.message.user.ConnectMessage;
import eleks.mentorship.bigbang.websocket.message.user.PositioningMessage;
import eleks.mentorship.bigbang.websocket.message.user.ReadyMessage;
import eleks.mentorship.bigbang.websocket.message.user.UserMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.tuple.MutablePair;
import org.reactivestreams.Publisher;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static eleks.mentorship.bigbang.websocket.Room.MAX_CONNECTIONS;

/**
 * Created by Emiliia Nesterovych on 7/10/2018.
 */
@Data
@AllArgsConstructor
public class GameEngine {
    private static final int EXPLOSION_DELAY = 5; // In seconds.
    private static final int EXPLOSION_RADIUS = 3; // Cells.

    private JsonMessageMapper mapper;
    private WebSocketMessageSubscriber messageSubscriber;
    private MessageAggregator aggregator;
    private GameState currentGameState;
    private Map<String, Boolean> playerReady;

    // TODO: inject game field (randomly).
    public GameEngine(JsonMessageMapper mapper, MessageAggregator aggregator) {
        this.mapper = mapper;
        currentGameState = new GameState(new HashMap<>(), new ArrayList<>(), new GameField("gamefield"));
        this.messageSubscriber = new WebSocketMessageSubscriber();
        this.aggregator = aggregator;
        playerReady = new HashMap<>();
    }

    public void buildGamePlay() {
        Flux<GameMessage> cache = messageSubscriber.getOutputEvents()
                .cache(1)
                ;
        messageSubscriber.setOutputEvents(
                cache.filter(x -> x instanceof ReadyMessage || x instanceof RoomStateMessage)
                        .map(msg -> {
                                    if (msg instanceof ReadyMessage) {
                                        playerReady.put(((ReadyMessage) msg).getPlayer().getNickname(), true);
                                        if (playerReady.size() == MAX_CONNECTIONS) {
                                            new StartCounterMessage();
                                        } else {
                                            new RoomStateMessage(playerReady);
                                        }
                                    }
                                    return msg;
                                }
                        )
        );
//
//        cache
//                .filter(x -> !(x instanceof PositioningMessage) && !(x instanceof StartCounterMessage)
//                        && !(x instanceof ReadyMessage))
//                .doOnNext(msg -> {
//                    if (msg instanceof BombExplosionMessage) {
//                        onBombExplosion((BombExplosionMessage) msg);
//                    }
//                })
//                .mergeWith(cache
//                        .filter(x -> x instanceof PositioningMessage)
//                        .map(x -> {
//                            x.setOccurrence(LocalDateTime.now());
//                            return (PositioningMessage) x;
//                        })
//                        .buffer(Duration.ofSeconds(2))
//                        .flatMap(messages ->
//                                aggregator.aggregate(messages, currentGameState))
//                )
//                .mergeWith(cache
//                        .filter(x -> x instanceof StartCounterMessage)
//                        .delaySubscription(Duration.ofSeconds(3))
//                        .map(x -> new GameStartMessage()))); // TODO: ignore messages from dead players.
    }

    private void onBombExplosion(BombExplosionMessage explosionMessage) {
        GamePlayer gamePlayer = explosionMessage.getOwner();
        GamePlayer fieldPlayer = currentGameState.getPlayers().stream()
                .filter(x -> x.getPlayer().getNickname().equals(gamePlayer.getPlayer().getNickname()))
                .findFirst()
                .orElseThrow(UserMissingException::new);
        fieldPlayer.setBombsLeft(fieldPlayer.getBombsLeft() + 1);
        GameField gameField = currentGameState.getGameField();
        int width = gameField.getWidth();
        int height = gameField.getHeight();

        Position bombPosition = explosionMessage.getPosition();
        // TODO: decrease HP of players in explosion range.

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

    public Flux<GameMessage> subscribePlayer(Flux<UserMessage> messageFluxCache, WebSocketSession session, final Map<String, WebSocketSession> players) {
        return messageFluxCache
                .filter(x -> x instanceof ConnectMessage)
                .take(1)
                .doOnNext(x -> {
                    registerPlayer(x, session, players);
                })
                .map(x->new RoomStateMessage(playerReady))
//                .ignoreElements()
                ;
    }

    private void registerPlayer(UserMessage userMessage, WebSocketSession session, final Map<String, WebSocketSession> players) {
        Player player = userMessage.getPlayer();
        players.put(player.getNickname(), session);
        GamePlayer gamePlayer = new GamePlayer(player);
        currentGameState.getPlayersMovesTime().put(player.getNickname(),
                MutablePair.of(gamePlayer, LocalDateTime.now()));
        //TODO: inject position.
        currentGameState.getPlayers().add(gamePlayer);
        playerReady.put(player.getNickname(), false);
    }

    public Flux<GameMessage> getGameFlow() {
        return messageSubscriber
                .getOutputEvents();
    }

    public void setGameFlow(Flux<GameMessage> flux) {
        messageSubscriber.setOutputEvents(flux);
    }
}
