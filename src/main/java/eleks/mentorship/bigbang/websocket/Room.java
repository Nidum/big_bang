package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.mapper.JsonEventMapper;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.GameStartMessage;
import eleks.mentorship.bigbang.websocket.message.PositioningMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

import javax.inject.Inject;
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

    private JsonEventMapper mapper;
    private WebSocketMessageSubscriber messageSubscriber;
    private UnicastProcessor<GameMessage> eventPublisher;
    private Flux<GameMessage> outputEvents;

    public Room() {
        name = UUID.randomUUID().toString();
        players = new HashSet<>();
        this.eventPublisher = UnicastProcessor.create();
        this.outputEvents = eventPublisher
                .replay(25)
                .autoConnect()
//        .skipWhile(x ->
//                players.size() < MAX_CONNECTIONS)
        ;
        this.messageSubscriber = new WebSocketMessageSubscriber(eventPublisher);
    }

    public Room(String name) {
        this();
        this.name = name;
    }

    public void startGame() {
        outputEvents = outputEvents.concatWith(Mono.just(new GameStartMessage()));
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public boolean isFilled() {
        return players.size() == MAX_CONNECTIONS;
    }

    public void addPlayer(WebSocketSession session) {
        players.add(session);
        session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(mapper::toEvent)
                .subscribe(messageSubscriber::onNext, messageSubscriber::onError);

        if (players.size() == MAX_CONNECTIONS) {
            this.startGame();
        }
    }
}
