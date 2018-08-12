package eleks.mentorship.bigbang.websocket;

import lombok.Data;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.util.Arrays;
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
    private Flux<WebSocketMessage> messageFlux = Flux.empty();
    private ConnectableFlux<WebSocketMessage> connectableMessageFlux;

    public Room() {
        players = new HashSet<>();
        name = UUID.randomUUID().toString();
    }

    public Room(String name) {
        this.name = name;
    }

//    public void startGame() {
////        players.forEach(player->gameFlow.subscribe(x->{
////            player.send(Flux.just(x));
////        }));
////        gameFlow.connect();
////        gameFlow.doOnEach(x -> {
////            players.forEach(z -> z.send(Mono.just(x.get())));
////        });
//    }

    public void startGame(){
        connectableMessageFlux = messageFlux.log().publish();
        connectableMessageFlux.connect();
    }

    //TODO: Add watchers.

    public boolean isEmpty() {
        return players.isEmpty(); // && watchers.isEmpty()
    }

    public boolean isFilled() {
        return players.size() == MAX_CONNECTIONS;
    }

    public void addPlayer(WebSocketSession player) {
        players.add(player);
        messageFlux = messageFlux.concatWith(player.receive());
        if (players.size() == MAX_CONNECTIONS) {
            this.startGame();
        }
    }
}
