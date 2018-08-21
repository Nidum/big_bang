package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.websocket.message.PositioningMessage;
import reactor.core.publisher.UnicastProcessor;

/**
 * Created by Emiliia Nesterovych on 8/20/2018.
 */
public class WebSocketMessageSubscriber {
    private UnicastProcessor<PositioningMessage> eventPublisher;

    public WebSocketMessageSubscriber(UnicastProcessor<PositioningMessage> eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void onNext(PositioningMessage event) {
        eventPublisher.onNext(event);
    }

    public void onError(Throwable error) {
        //TODO log error
        error.printStackTrace();
    }
}