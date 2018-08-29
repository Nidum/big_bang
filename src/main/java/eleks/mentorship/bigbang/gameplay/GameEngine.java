package eleks.mentorship.bigbang.gameplay;

import eleks.mentorship.bigbang.Player;
import eleks.mentorship.bigbang.common.exception.UserMissingException;
import eleks.mentorship.bigbang.mapper.JsonMessageMapper;
import eleks.mentorship.bigbang.util.Position;
import eleks.mentorship.bigbang.websocket.MessageAggregator;
import eleks.mentorship.bigbang.websocket.WebSocketMessageSubscriber;
import eleks.mentorship.bigbang.websocket.message.BombExplosionMessage;
import eleks.mentorship.bigbang.websocket.message.GameState;
import eleks.mentorship.bigbang.websocket.message.NewPlayerMessage;
import eleks.mentorship.bigbang.websocket.message.UserMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.tuple.MutablePair;
import org.reactivestreams.Publisher;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    // TODO: inject game field (randomly).
    public GameEngine(JsonMessageMapper mapper, MessageAggregator aggregator) {
        this.mapper = mapper;
        currentGameState = new GameState(new HashMap<>(), new ArrayList<>(), new GameField("gamefield"));
        this.messageSubscriber = new WebSocketMessageSubscriber();
        this.aggregator = aggregator;
    }

    public void prepareToGame() {
        messageSubscriber.setOutputEvents(messageSubscriber.getOutputEvents()
                .filter(x -> x instanceof UserMessage)
                .map(x -> {
                    x.setOccurrence(LocalDateTime.now());
                    return (UserMessage) x;
                })
                .buffer(Duration.ofSeconds(2))
                .flatMap(messages ->
                        aggregator.aggregate(messages, currentGameState))
                .doOnNext(msg -> {
                    if (msg instanceof BombExplosionMessage) {
                        BombExplosionMessage bombMsg = (BombExplosionMessage) msg;
                        GamePlayer gamePlayer = bombMsg.getOwner();
                        GamePlayer fieldPlayer = currentGameState.getPlayers().stream()
                                .filter(x -> x.getPlayer().getNickname().equals(gamePlayer.getPlayer().getNickname()))
                                .findFirst()
                                .orElseThrow(UserMissingException::new);
                        fieldPlayer.setBombsLeft(fieldPlayer.getBombsLeft() + 1);
                        GameField gameField = currentGameState.getGameField();
                        int width = gameField.getWidth();
                        int height = gameField.getHeight();

                        Position bombPosition = bombMsg.getPosition();
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
                        bombMsg.setDamaged(damagedPlayers);
                    }
                })); // TODO: ignore messages from dead players.
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

    public void subscribePlayer(WebSocketSession session) {
//        session.send(Mono.just(currentGameState)
//                .map(x->mapper.toJSON(x))
//                .map(session::textMessage));
        session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(mapper::toUserMessage)
                .subscribe(messageSubscriber::onNext, messageSubscriber::onError);


        GamePlayer gamePlayer = new GamePlayer(
                new Player(UUID.randomUUID(), "BOOMer")
                , new Position(0, 1));
        currentGameState.getPlayersMovesTime().put(gamePlayer.getPlayer().getNickname(), MutablePair.of(gamePlayer, LocalDateTime.now()));
        currentGameState.getPlayers().add(gamePlayer);
        messageSubscriber.onNext(new NewPlayerMessage(gamePlayer.getPlayer())); //TODO: add to new player message player info.
    }

    public Publisher<WebSocketMessage> getGameFlow(WebSocketSession session) {
        return messageSubscriber
                .getOutputEvents()
                .map(mapper::toJSON)
                .map(session::textMessage);
    }
}
