package eleks.mentorship.bigbang.gameplay;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eleks.mentorship.bigbang.config.SystemConfig;
import eleks.mentorship.bigbang.websocket.Room;
import eleks.mentorship.bigbang.websocket.message.PositioningMessage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;

import javax.inject.Inject;
import java.time.Duration;

/**
 * Created by Emiliia Nesterovych on 7/10/2018.
 */
@Component
@AllArgsConstructor
public class GameEngine {
    private ObjectMapper objectMapper;
    private static final int EXPLOSION_DELAY = 5; // In seconds.

    public Flux<WebSocketMessage> handle(Room room, WebSocketSession session, PositioningMessage message) {
        switch (message.getType()) {
            case MOVE:
                return handleMove(message, session);
            case BOMB_PLACEMENT:
                return handleBombPlacement(message, session);
            default:
                return Flux.error(new RuntimeException("Unknown message received."));
        }
    }

    private Flux<WebSocketMessage> handleMove(PositioningMessage message, WebSocketSession session) {
        return null;
    }

    private Flux<WebSocketMessage> handleBombPlacement(PositioningMessage message, WebSocketSession session) {
        GamePlayer player = message.getGamePlayer();
        int bombsLeft = player.getBombsLeft();
        if (bombsLeft < 0) {
            return Flux.empty();
        }
        player.setBombsLeft(bombsLeft - 1);
        return Flux.just(message)
                .delayElements(Duration.ofSeconds(EXPLOSION_DELAY))
                .map(value -> {
                    try {
                        return objectMapper.writeValueAsString(value);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(session::textMessage);
    }
}
