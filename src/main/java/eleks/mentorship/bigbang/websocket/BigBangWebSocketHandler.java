package eleks.mentorship.bigbang.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import eleks.mentorship.bigbang.gameplay.GameEngine;
import eleks.mentorship.bigbang.websocket.message.PositionMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import java.io.IOException;

@Component
//@AllArgsConstructor
public class BigBangWebSocketHandler implements WebSocketHandler {

    @Inject
    private ObjectMapper objectMapper;
    @Inject
    private GameEngine engine;
    @Inject
    private RoomManager roomManager;
//
//    private final Flux<String> eventFlux = Flux.generate(sink -> {
//        GameField field = new GameField("gamefield");
//        GamePlayer player1 = new GamePlayer(new Player("testUser"));
//        GamePlayer player2 = new GamePlayer(new Player("awesome_unicorn"));
//        GamePlayer player3 = new GamePlayer(new Player("xxx_BOMBER_xxx"));
//
//        try {
//            sink.next(objectMapper.writeValueAsString(new GameState(Arrays.asList(player1, player2, player3), field)));
//        } catch (JsonProcessingException e) {
//            sink.error(e);
//        }
//    });
//
//    private final Flux<String> intervalFlux = Flux.interval(Duration.ofMillis(1000L))
//            .zipWith(eventFlux, (time, event) -> event);

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        Room room = roomManager.assignUserToRoom(webSocketSession);
        Flux<WebSocketMessage> serverMessages = webSocketSession
                .receive()
                .map(WebSocketMessage::getPayloadAsText)
                //.take(Duration.ofMillis(1000L)) //handle data every second?
                .flatMap(message -> {
                    try {
                        return engine.handle(room, webSocketSession, objectMapper.readValue(message, PositionMessage.class));
                    } catch (IOException e) {
                        throw new RuntimeException("PositionMessage can't be parsed", e);
                    }
                })
                .doFinally(x -> room.getPlayers().remove(webSocketSession));
//        room.getPlayers().stream()
//                .filter(player -> player != webSocketSession)
//                .forEach(player -> player.send(Flux.from(serverMessages)));
        room.setGameFlow(room.getGameFlow().concatWith(serverMessages).publish());
        return webSocketSession.send(room.getGameFlow());
    }
//        return webSocketSession
//                .send(intervalFlux
//                        .map(webSocketSession::textMessage))
//                .and(webSocketSession.receive()
//                        .map((event) -> "dsa")
//                );
//    }
}
