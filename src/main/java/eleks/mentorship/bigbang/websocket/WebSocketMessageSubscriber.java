package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.gameplay.GameField;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.GameState;
import eleks.mentorship.bigbang.websocket.message.UserMessage;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by Emiliia Nesterovych on 8/24/2018.
 */
@Data
public class WebSocketMessageSubscriber {
    private UnicastProcessor<GameMessage> eventPublisher;
    private Flux<GameMessage> outputEvents;
    private MessageAggregator aggregator = new MessageAggregator();
    private GameState currentGameState = new GameState(new ArrayList<>(), new GameField("gamefield"));

    public WebSocketMessageSubscriber() {
        this.eventPublisher = UnicastProcessor.create();
        this.outputEvents = eventPublisher
                .replay()
                .autoConnect()
                .filter(x -> x instanceof UserMessage)
                .groupBy(GameMessage::getClass)
                .flatMap(group -> group.buffer(Duration.ofSeconds(5))
                        .map(messages ->
                                messages.stream().map(x -> (UserMessage) x).collect(Collectors.toList()))
                        .map(messages ->
                                aggregator.aggregate(messages, currentGameState, EUserMessageType.findByClass(group.key()))));
    }

    public void onNext(GameMessage event) {
        eventPublisher.onNext(event);
    }

    public void onError(Throwable error) {
        //TODO log error
        error.printStackTrace();
    }
}
