package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.gameplay.GameEngine;
import eleks.mentorship.bigbang.mapper.JsonMessageMapper;
import lombok.Data;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Emiliia Nesterovych on 7/15/2018.
 */
@Data
public class Room {
    public static final int MAX_CONNECTIONS = 2;

    private String name;
    private Map<String, WebSocketSession> players;
    private GameEngine engine;

    private boolean gameStarted = false;

    public Room(JsonMessageMapper mapper, MessageAggregator aggregator) {
        name = UUID.randomUUID().toString();
        players = new HashMap<>();
        engine = new GameEngine(mapper, aggregator);
    }

    public Room(JsonMessageMapper mapper, MessageAggregator aggregator, String name) {
        this(mapper, aggregator);
        this.name = name;
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public boolean isFilled() {
        return players.size() == MAX_CONNECTIONS;
    }

    public void registerPlayer(WebSocketSession session) {
        if(players.isEmpty()){
            engine.buildGamePlay();
        }
        engine.subscribePlayer(session, players);
    }
}
