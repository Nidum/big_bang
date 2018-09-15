package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.gameplay.GameEngine;
import eleks.mentorship.bigbang.mapper.JsonMessageMapper;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.user.UserMessage;
import lombok.Data;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    public Flux<GameMessage> registerPlayer(Flux<UserMessage> userMessageFlux) {
        if (players.isEmpty()) {
            engine.buildGamePlay();
        }
        return engine.subscribePlayer(userMessageFlux);
    }
}
