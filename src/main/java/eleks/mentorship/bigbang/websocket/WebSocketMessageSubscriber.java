package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.websocket.message.GameMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

@Slf4j
@Data
public class WebSocketMessageSubscriber {
    private UnicastProcessor<GameMessage> eventPublisher;
    private Flux<GameMessage> outputEvents;
    private MessageAggregator aggregator = new MessageAggregator();

    public WebSocketMessageSubscriber() {
        this.eventPublisher = UnicastProcessor.create();
        this.outputEvents = eventPublisher;
    }

    public void onNext(GameMessage event) {
        eventPublisher.onNext(event);
    }

    public void onError(Throwable error) {
        log.error(error.getMessage());
    }
}
