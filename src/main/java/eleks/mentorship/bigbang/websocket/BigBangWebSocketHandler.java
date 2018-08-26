package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.mapper.JsonMessageMapper;
import eleks.mentorship.bigbang.websocket.message.GameState;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class BigBangWebSocketHandler implements WebSocketHandler {
    private RoomManager roomManager;
    private JsonMessageMapper mapper;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Room room = roomManager.assignUserToRoom(session);
        GameState currentGameState = room.getEngine().getCurrentGameState();
        return session.send(Mono.just(currentGameState)
                .map(x -> mapper.toJSON(x))
                .map(session::textMessage)
                .concatWith(room.getEngine().getGameFlow(session)));
    }

}
