package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.gameplay.GameEngine;
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

import java.time.Duration;
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

    private JsonEventMapper mapper;
    private WebSocketMessageSubscriber messageSubscriber;
    private UnicastProcessor<GameMessage> eventPublisher;
    private Flux<GameMessage> outputEvents;

    public Room() {
        name = UUID.randomUUID().toString();
        players = new HashSet<>();
        this.eventPublisher = UnicastProcessor.create();
        Mono.just(new GameStartMessage()).delayUntil(x -> Flux.interval(Duration.ofSeconds(1)).just(players.size() < MAX_CONNECTIONS));
        this.outputEvents = eventPublisher
                .replay(25)
                .autoConnect();
        this.messageSubscriber = new WebSocketMessageSubscriber(eventPublisher);
    }

    public Room(String name) {
        this();
        this.name = name;
    }

    public void startGame() {
        eventPublisher.sink().next(new GameStartMessage());
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

    public static void main(String[] args) {
//   Aggregate packets.
//        Flux.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
//                .delayElements(Duration.ofMillis(250))
//                .window(Duration.ofSeconds(1))
//                .flatMap(innerFlux -> innerFlux.collectList()
//                        .map(list -> list.stream().reduce(Integer::sum).orElse(0)))
//                .log()
//                .subscribe();
//   Aggregate packets using buffer
//        Flux.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
//                .delayElements(Duration.ofMillis(250))
//                .buffer(Duration.ofSeconds(1))
//                .map(list -> list.stream().reduce(Integer::sum).orElse(0))
//                .log()
//                .subscribe();

        // When everyone is ready - start game.
//        final int[] count = {0};
//        Flux.just(0, 1, 2, 3, 4,
//                5, 6, 4,
//                7, 8, 9, 10, 11, 4,
//                5, 4, 3, 2, 1)
//                .delayElements(Duration.ofMillis(250))
//                .skipUntil(x -> {
//                    if (x == 4) count[0]++;
//                    return count[0] == 3;
//                })
//                .delaySubscription(Duration.ofSeconds(3))
//                .log()
//                .subscribe();


//        // TODO: try insert some events in between.
//        final int[] count = {0};
//        Flux.just(0, 1, 2, 3, 5, 6, 7, 8, 9, 10, 11)
//                .delayElements(Duration.ofMillis(250))
//
//                .delaySubscription(Duration.ofSeconds(1))
//                .log()
//                .subscribe();


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
