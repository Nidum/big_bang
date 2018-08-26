package eleks.mentorship.bigbang.gameplay;

import eleks.mentorship.bigbang.Player;
import eleks.mentorship.bigbang.mapper.JsonMessageMapper;
import eleks.mentorship.bigbang.util.Position;
import eleks.mentorship.bigbang.websocket.MessageAggregator;
import eleks.mentorship.bigbang.websocket.WebSocketMessageSubscriber;
import eleks.mentorship.bigbang.websocket.message.GameState;
import eleks.mentorship.bigbang.websocket.message.NewPlayerMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.tuple.MutablePair;
import org.reactivestreams.Publisher;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Emiliia Nesterovych on 7/10/2018.
 */
@Data
@AllArgsConstructor
public class GameEngine {
    private static final int EXPLOSION_DELAY = 5; // In seconds.

    private JsonMessageMapper mapper;
    private WebSocketMessageSubscriber messageSubscriber;
    private MessageAggregator aggregator;
    private GameState currentGameState;

    // TODO: inject game field (randomly).
    public GameEngine(JsonMessageMapper mapper, MessageAggregator aggregator) {
        this.mapper = mapper;
        currentGameState = new GameState(new HashMap<>(), new ArrayList<>(), new GameField("gamefield"));
        this.messageSubscriber = new WebSocketMessageSubscriber(currentGameState);
        this.aggregator = aggregator;
    }

    public void subscribePlayer(WebSocketSession session) {
        session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(mapper::toUserMessage)
                .subscribe(messageSubscriber::onNext, messageSubscriber::onError);

        GamePlayer gamePlayer = new GamePlayer(
                new Player(UUID.randomUUID(), "BOOMer")
                , new Position(0, 1));
        currentGameState.getPlayersMovesTime().put(gamePlayer.getPlayer().getNickname(), MutablePair.of(gamePlayer, LocalDateTime.now()));
        currentGameState.getPlayers().add(gamePlayer);

        messageSubscriber.onNext(new NewPlayerMessage()); //TODO: add to new player message player info.
    }

    public Publisher<WebSocketMessage> getGameFlow(WebSocketSession session) {
        return messageSubscriber
                .getOutputEvents()
                .map(mapper::toJSON)
                .map(session::textMessage);
    }
}
