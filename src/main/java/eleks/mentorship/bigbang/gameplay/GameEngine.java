package eleks.mentorship.bigbang.gameplay;

import eleks.mentorship.bigbang.mapper.JsonMessageMapper;
import eleks.mentorship.bigbang.websocket.Room;
import eleks.mentorship.bigbang.websocket.WebSocketMessageSubscriber;
import eleks.mentorship.bigbang.websocket.message.PositioningMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.reactivestreams.Publisher;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * Created by Emiliia Nesterovych on 7/10/2018.
 */
@Data
@AllArgsConstructor
public class GameEngine {
    private static final int EXPLOSION_DELAY = 5; // In seconds.

    private JsonMessageMapper mapper;
    private WebSocketMessageSubscriber messageSubscriber;

    public void subscribePlayer(WebSocketSession session){
        session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(mapper::toMessage)
                .subscribe(messageSubscriber::onNext, messageSubscriber::onError);
    }

    public Publisher<WebSocketMessage> getOutputEvents(WebSocketSession session) {
        return messageSubscriber.getOutputEvents().map(mapper::toJSON).map(session::textMessage);
    }

    public Flux<WebSocketMessage> handle(Room room, WebSocketSession session, PositioningMessage message) {
//        switch (message.getType()) {
//            case MOVE:
//                return handleMove(message, session);
//            case BOMB_PLACEMENT:
//                return handleBombPlacement(message, session);
//            default:
//                return Flux.error(new RuntimeException("Unknown message received."));
//        }
        return null;
    }

    private Flux<WebSocketMessage> handleMove(PositioningMessage message, WebSocketSession session) {
        return null;
    }

    private Flux<WebSocketMessage> handleBombPlacement(PositioningMessage message, WebSocketSession session) {
        GamePlayer player = message.getGamePlayer();
        int bombsLeft = player.getBombsLeft();
        if (bombsLeft < 0) {
            return Flux.empty();
        }
        player.setBombsLeft(bombsLeft - 1);
        return Flux.just(message)
                .delayElements(Duration.ofSeconds(EXPLOSION_DELAY))
                .map(value -> mapper.toJSON(value))
                .map(session::textMessage);
    }
}
