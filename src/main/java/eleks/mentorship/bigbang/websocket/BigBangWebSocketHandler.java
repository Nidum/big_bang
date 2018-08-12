package eleks.mentorship.bigbang.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eleks.mentorship.bigbang.gameplay.GameEngine;
import eleks.mentorship.bigbang.websocket.message.PositionMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

@Component
//@AllArgsConstructor
public class BigBangWebSocketHandler implements WebSocketHandler {

    //
//    @Inject
//    private ObjectMapper objectMapper;
//    @Inject
//    private GameEngine engine;
//    @Inject
//    private RoomManager roomManager;
////
////    private final Flux<String> eventFlux = Flux.generate(sink -> {
////        GameField field = new GameField("gamefield");
////        GamePlayer player1 = new GamePlayer(new Player("testUser"));
////        GamePlayer player2 = new GamePlayer(new Player("awesome_unicorn"));
////        GamePlayer player3 = new GamePlayer(new Player("xxx_BOMBER_xxx"));
////
////        try {
////            sink.next(objectMapper.writeValueAsString(new GameState(Arrays.asList(player1, player2, player3), field)));
////        } catch (JsonProcessingException e) {
////            sink.error(e);
////        }
////    });
////
////    private final Flux<WebSocketMessage> intervalFlux = Flux.interval(Duration.ofMillis(1000L));
//
//    @Override
//    public Mono<Void> handle(WebSocketSession webSocketSession) {
//        Room room = roomManager.assignUserToRoom(webSocketSession);
//        Flux<WebSocketMessage> serverMessages = webSocketSession
//                .receive()
//                .map(WebSocketMessage::getPayloadAsText)
//                .flatMap(message -> {
//                    try {
//                        return engine.handle(room, webSocketSession, objectMapper.readValue(message, PositionMessage.class));
//                    } catch (IOException e) {
//                        throw new RuntimeException("PositionMessage can't be parsed", e);
//                    }
//                })
//                .doFinally(x -> room.getPlayers().remove(webSocketSession))
//                .cache();
//        return webSocketSession.send(serverMessages);
//    }
////        return webSocketSession
////                .send(intervalFlux
////                        .map(webSocketSession::textMessage))
////                .and(webSocketSession.receive()
////                        .map((event) -> "dsa")
////                );
////    }

    private UnicastProcessor<PositionMessage> eventPublisher;
    private Flux<String> outputEvents;
    private ObjectMapper mapper;

    public BigBangWebSocketHandler(UnicastProcessor<PositionMessage> eventPublisher, Flux<PositionMessage> events) {
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
                .subscribe(subscriber::onNext, subscriber::onError, subscriber::onComplete);
        return session.send(outputEvents.map(session::textMessage));
    }


    private PositionMessage toEvent(String json) {
        try {
            return mapper.readValue(json, PositionMessage.class);
        } catch (IOException e) {
            throw new RuntimeException("Invalid JSON:" + json, e);
        }
    }

    private String toJSON(PositionMessage event) {
        try {
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static class WebSocketMessageSubscriber {
        private UnicastProcessor<PositionMessage> eventPublisher;
        private Optional<PositionMessage> lastReceivedEvent = Optional.empty();

        public WebSocketMessageSubscriber(UnicastProcessor<PositionMessage> eventPublisher) {
            this.eventPublisher = eventPublisher;
        }

        public void onNext(PositionMessage event) {
            lastReceivedEvent = Optional.of(event);
            eventPublisher.onNext(event);
        }

        public void onError(Throwable error) {
            //TODO log error
            error.printStackTrace();
        }

        public void onComplete() {
//            lastReceivedEvent.ifPresent(event -> eventPublisher.onNext(
//                    Event.type(USER_LEFT)
//                            .withPayload()
//                            .user(event.getUser())
//                            .build()));
        }

    }
}
