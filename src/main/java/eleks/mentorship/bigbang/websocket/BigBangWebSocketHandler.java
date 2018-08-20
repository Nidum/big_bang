package eleks.mentorship.bigbang.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eleks.mentorship.bigbang.websocket.message.PositioningMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

import java.io.IOException;

@Component
public class BigBangWebSocketHandler implements WebSocketHandler {
    private UnicastProcessor<PositioningMessage> eventPublisher;
    private Flux<String> outputEvents;
    private ObjectMapper mapper;

    public BigBangWebSocketHandler(UnicastProcessor<PositioningMessage> eventPublisher, Flux<PositioningMessage> events) {
        this.eventPublisher = eventPublisher;
        this.mapper = new ObjectMapper();
        this.outputEvents = Flux.from(events).map(this::toJSON);
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        WebSocketMessageSubscriber subscriber = new WebSocketMessageSubscriber(eventPublisher);
        session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(this::toEvent)
                .subscribe(subscriber::onNext, subscriber::onError);
        return session.send(outputEvents.map(session::textMessage));
    }

    private PositioningMessage toEvent(String json) {
        try {
            return mapper.readValue(json, PositioningMessage.class);
        } catch (IOException e) {
            throw new RuntimeException("Invalid JSON:" + json, e);
        }
    }

    private String toJSON(PositioningMessage event) {
        try {
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static class WebSocketMessageSubscriber {
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
}
