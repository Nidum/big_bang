package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.mapper.JsonMessageMapper;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.server.RoomStateMessage;
import eleks.mentorship.bigbang.websocket.message.server.StartCounterMessage;
import eleks.mentorship.bigbang.websocket.message.user.ReadyMessage;
import eleks.mentorship.bigbang.websocket.message.user.UserMessage;
import lombok.AllArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

import java.time.Duration;
import java.util.Map;

import static eleks.mentorship.bigbang.websocket.Room.MAX_CONNECTIONS;

@Component
@AllArgsConstructor
public class BigBangWebSocketHandler implements WebSocketHandler {
    private RoomManager roomManager;
    private JsonMessageMapper mapper;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Room room = roomManager.assignUserToRoom();
        Flux<UserMessage> userMessageFlux = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(x -> mapper.toUserMessage(x))
                .replay()
                .autoConnect();
        Flux<GameMessage> userConnectionFlux = room.registerPlayer(userMessageFlux, session);
        WebSocketMessageSubscriber eventPublisher = room.getEngine().getMessageSubscriber();
        userConnectionFlux
                .subscribe(eventPublisher::onNext, eventPublisher::onError);
        userMessageFlux
                .subscribe(eventPublisher::onNext, eventPublisher::onError);
        Flux<GameMessage> gameFlow = room.getEngine().getGameFlow();

        return session.send(
                userConnectionFlux
                        .map(x -> mapper.toJSON(x))
                        .map(session::textMessage)
                        .ignoreElements()
                        .concatWith(
                                gameFlow
                                        .map(mapper::toJSON)
                                        .map(session::textMessage)
                        )
//        )
        );
    }

}
