package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.gameplay.GameEngine;
import eleks.mentorship.bigbang.mapper.JsonMessageMapper;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.user.UserMessage;
import lombok.Data;
import reactor.core.publisher.Flux;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class Room {
    public static final int MAX_CONNECTIONS = 2;

    private String name;
    private GameEngine engine;
    private AtomicInteger userCount = new AtomicInteger(0);

    private boolean gameStarted = false;

    public Room(JsonMessageMapper mapper, MessageAggregator aggregator) {
        name = UUID.randomUUID().toString();
        engine = new GameEngine(mapper, aggregator);
    }

    public Room(JsonMessageMapper mapper, MessageAggregator aggregator, String name) {
        this(mapper, aggregator);
        this.name = name;
    }

    public boolean isEmpty() {
        return userCount.get() == 0;
    }

    public boolean isFilled() {
        return userCount.get() == MAX_CONNECTIONS;
    }

    public Flux<GameMessage> registerPlayer(Flux<UserMessage> userMessageFlux) {
        userCount.incrementAndGet();
        if (userCount.get() == MAX_CONNECTIONS) {
            gameStarted = true;
        }
        return engine.subscribePlayer(userMessageFlux);
    }
}
