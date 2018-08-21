package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.mapper.JsonEventMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class BigBangWebSocketHandler implements WebSocketHandler {
    private JsonEventMapper mapper;
    private RoomManager roomManager;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Room room = roomManager.assignUserToRoom(session);
        return session.send(room.getOutputEvents().map(mapper::toJSON).map(session::textMessage));
    }


}
