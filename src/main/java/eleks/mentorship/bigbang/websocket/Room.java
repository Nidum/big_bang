package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.gameplay.GameEngine;
import eleks.mentorship.bigbang.mapper.JsonMessageMapper;
import lombok.Data;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Emiliia Nesterovych on 7/15/2018.
 */
@Data
public class Room {
    private static final int MAX_CONNECTIONS = 2;

    private String name;
    private Set<WebSocketSession> players;
    private GameEngine engine;

    public Room(JsonMessageMapper mapper) {
        name = UUID.randomUUID().toString();
        players = new HashSet<>();
        engine = new GameEngine(mapper, new WebSocketMessageSubscriber());
    }

    public Room(JsonMessageMapper mapper, String name) {
        this(mapper);
        this.name = name;
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public boolean isFilled() {
        return players.size() == MAX_CONNECTIONS;
    }

    public void addPlayer(WebSocketSession session) {
        players.add(session);
        engine.subscribePlayer(session);
    }
}
