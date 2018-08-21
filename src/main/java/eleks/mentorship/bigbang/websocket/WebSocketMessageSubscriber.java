package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.websocket.message.GameMessage;
import reactor.core.publisher.UnicastProcessor;

/**
 * Created by Emiliia Nesterovych on 8/20/2018.
 */
public class WebSocketMessageSubscriber {
    private UnicastProcessor<GameMessage> eventPublisher;

    public WebSocketMessageSubscriber(UnicastProcessor<GameMessage> eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void onNext(GameMessage event) {
        eventPublisher.onNext(event);
    }

    public void onError(Throwable error) {
        //TODO log error
        error.printStackTrace();
    }
}