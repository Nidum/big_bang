package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.gameplay.PlayerInfo;
import eleks.mentorship.bigbang.mapper.JsonMessageMapper;
import eleks.mentorship.bigbang.security.UserPrincipal;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.user.UserMessage;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
@AllArgsConstructor
public class BigBangWebSocketHandler implements WebSocketHandler {
    private RoomManager roomManager;
    private JsonMessageMapper mapper;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Room room = roomManager.findFreeRoom();

        Flux<UserMessage> userMessageFlux = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(msg -> mapper.toUserMessage(msg))
                .map(msg -> {
                    // TODO: accept user occurrence message and ignore ones from future, or older then TTL.
                    msg.setOccurrence(Instant.now());
                    return msg;
                })
                .replay()
                .autoConnect();

        Flux<GameMessage> userConnectionFlux = room.registerPlayer(userMessageFlux);
        WebSocketMessageSubscriber eventPublisher = room.getEngine().getMessageSubscriber();

        userConnectionFlux
                .subscribe(eventPublisher::onNext, eventPublisher::onError);
        userMessageFlux
                .subscribe(eventPublisher::onNext, eventPublisher::onError);

        Flux<GameMessage> gameFlow = room.getEngine().getGameFlow();

        Flux<WebSocketMessage> gameFlowMessageFlux = gameFlow
                .map(mapper::toJSON)
                .map(session::textMessage);

        return session.send(gameFlowMessageFlux);
    }

    private static Mono<PlayerInfo> currentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .map(p -> (UserPrincipal) p)
                .map(userPrincipal -> new PlayerInfo(userPrincipal.getId(), userPrincipal.getNickname()));
    }

}
