package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.mapper.JsonMessageMapper;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.user.UserMessage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class BigBangWebSocketHandler implements WebSocketHandler {
    private RoomManager roomManager;
    private JsonMessageMapper mapper;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Room room = roomManager.assignUserToRoom();
        Flux<UserMessage> map = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(x -> mapper.toUserMessage(x))
                .cache();
        Flux<GameMessage> flux = room.registerPlayer(map, session);
        return session.send(flux
                .map(x -> mapper.toJSON(x))
                .map(session::textMessage)
                .concatWith(room.getEngine().getGameFlow(session)));
    }

}
