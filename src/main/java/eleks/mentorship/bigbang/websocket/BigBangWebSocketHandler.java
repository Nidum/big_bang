package eleks.mentorship.bigbang.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eleks.mentorship.bigbang.Player;
import eleks.mentorship.bigbang.gameplay.GameEngine;
import eleks.mentorship.bigbang.gameplay.GamePlayer;
import eleks.mentorship.bigbang.gameplay.GameField;
import eleks.mentorship.bigbang.gameplay.GameState;
import eleks.mentorship.bigbang.websocket.message.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@Component
public class BigBangWebSocketHandler implements WebSocketHandler {

    private static final ObjectMapper json = new ObjectMapper();

    private Flux<String> eventFlux = Flux.generate(sink -> {
        GameField field = new GameField("gamefield");
        GamePlayer player1 = new GamePlayer(new Player("testUser"));
        GamePlayer player2 = new GamePlayer(new Player("awesome_unicorn"));
        GamePlayer player3 = new GamePlayer(new Player("xxx_BOMBER_xxx"));

        try {
            sink.next(json.writeValueAsString(new GameState(Arrays.asList(player1, player2, player3), field)));
        } catch (JsonProcessingException e) {
            sink.error(e);
        }
    });

    private Flux<String> intervalFlux = Flux.interval(Duration.ofMillis(1000L))
            .zipWith(eventFlux, (time, event) -> event);

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
//        return webSocketSession
//                .send(intervalFlux
//                        .map(webSocketSession::textMessage))
//                .and(webSocketSession.receive()
//                        .map(WebSocketMessage::getPayloadAsText)
//                        .take(Duration.ofMillis(100))
//                        .map(event->{
//
//                            return "dsa";
//                        })
//                        );
        ObjectMapper objectMapper = new ObjectMapper();
        GameEngine engine = new GameEngine();
        webSocketSession
                .receive()
                .map(WebSocketMessage::getPayloadAsText)
//                .map(message -> {
//                    try {
//                        return engine.handle(objectMapper.readValue(message, Message.class));
//                    } catch (IOException e) {
//                        throw new RuntimeException("Message can't be parsed", e);
//                    }
//                })
        ;


        return Mono.empty();
    }
}
