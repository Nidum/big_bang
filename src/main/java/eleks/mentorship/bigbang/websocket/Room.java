package eleks.mentorship.bigbang.websocket;

import lombok.Data;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

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
    private ConnectableFlux<WebSocketMessage> gameFlow;

    public Room() {
        players = new HashSet<>();
        name = UUID.randomUUID().toString();
        initGameFlow();
    }

    public Room(String name) {
        this.name = name;
    }

    public void startGame(){
        players.forEach(player->gameFlow.subscribe(x->{
            player.send(Flux.just(x));
        }));
        gameFlow.connect();
    }

    //TODO: Add watchers.

    public boolean isEmpty() {
        return players.isEmpty(); // && watchers.isEmpty()
    }

    public boolean isFilled() {
        return players.size() == MAX_CONNECTIONS;
    }

    private void initGameFlow() {
        //.skipUntil(x -> players.size() == MAX_CONNECTIONS)
        gameFlow = Flux.empty().map(x -> (WebSocketMessage) x).publish();
    }
    public void addPlayer(WebSocketSession player) {
        players.add(player);
        if(players.size() == MAX_CONNECTIONS){
            this.startGame();
        }
    }
}
