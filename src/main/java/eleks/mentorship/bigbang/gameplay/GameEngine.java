package eleks.mentorship.bigbang.gameplay;

import eleks.mentorship.bigbang.Player;
import eleks.mentorship.bigbang.mapper.JsonMessageMapper;
import eleks.mentorship.bigbang.util.Position;
import eleks.mentorship.bigbang.websocket.EUserMessageType;
import eleks.mentorship.bigbang.websocket.MessageAggregator;
import eleks.mentorship.bigbang.websocket.WebSocketMessageSubscriber;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.GameState;
import eleks.mentorship.bigbang.websocket.message.NewPlayerMessage;
import eleks.mentorship.bigbang.websocket.message.UserMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.reactivestreams.Publisher;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public GameEngine(JsonMessageMapper mapper, WebSocketMessageSubscriber messageSubscriber, MessageAggregator aggregator) {
        this.mapper = mapper;
        this.messageSubscriber = messageSubscriber;
        this.aggregator = aggregator;
        currentGameState = new GameState(new ArrayList<>(), new GameField("gamefield"));
    }

    public void subscribePlayer(WebSocketSession session) {
        session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(mapper::toUserMessage)
                .subscribe(messageSubscriber::onNext, messageSubscriber::onError)
        ;

        currentGameState.getPlayers().add(
                new GamePlayer(
                        new Player(UUID.randomUUID(), "Lol" + System.currentTimeMillis() % 100)
                        , new Position(0, 1)));
        messageSubscriber.onNext(new NewPlayerMessage()); //TODO: add to new player message player info.
        //startGame();
    }

    public Publisher<WebSocketMessage> getGameFlow(WebSocketSession session) {
        return messageSubscriber
                .getOutputEvents()
                .map(mapper::toJSON)
                .map(session::textMessage);
    }

    public void startGame() {
        messageSubscriber.getEventPublisher()
                .filter(x -> x instanceof UserMessage)
                .groupBy(GameMessage::getClass)
                .doOnNext(group -> {
                    group.buffer(Duration.ofSeconds(5))
                            .map(messages ->
                                    messages.stream().map(x -> (UserMessage) x).collect(Collectors.toList()))
                            .map(messages ->
                                    aggregator.aggregate(messages, currentGameState, EUserMessageType.findByClass(group.key())))
                            .subscribe(messageSubscriber::onNext, messageSubscriber::onError);
                });
    }
}
