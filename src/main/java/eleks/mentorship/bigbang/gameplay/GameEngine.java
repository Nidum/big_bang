package eleks.mentorship.bigbang.gameplay;

import eleks.mentorship.bigbang.Player;
import eleks.mentorship.bigbang.mapper.JsonMessageMapper;
import eleks.mentorship.bigbang.util.Position;
import eleks.mentorship.bigbang.websocket.MessageAggregator;
import eleks.mentorship.bigbang.websocket.WebSocketMessageSubscriber;
import eleks.mentorship.bigbang.websocket.message.GameState;
import eleks.mentorship.bigbang.websocket.message.NewPlayerMessage;
import eleks.mentorship.bigbang.websocket.message.UserMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.reactivestreams.Publisher;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Emiliia Nesterovych on 7/10/2018.
 */
@Data
@AllArgsConstructor
public class GameEngine {
    private static final int EXPLOSION_DELAY = 5; // In seconds.

    private JsonMessageMapper mapper;
    private WebSocketMessageSubscriber messageSubscriber;
    private MessageAggregator aggregator;
    private GameState currentGameState;

    // TODO: inject game field (randomly).
    public GameEngine(JsonMessageMapper mapper, WebSocketMessageSubscriber messageSubscriber, MessageAggregator aggregator) {
        this.mapper = mapper;
        this.messageSubscriber = messageSubscriber;
        this.aggregator = aggregator;
        currentGameState = new GameState(new ArrayList<>(), new GameField("gamefield"));
    }

    public void subscribePlayer(WebSocketSession session) {
        session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(mapper::toUserMessage)
                .buffer(Duration.ofSeconds(2))
                .map(list -> {
                    currentGameState = aggregator.aggregate(list, currentGameState);
                    return currentGameState;
                })
                .subscribe(messageSubscriber::onNext, messageSubscriber::onError);
        currentGameState.getPlayers().add(
                new GamePlayer(
                        new Player(UUID.randomUUID(), "Lol" + System.currentTimeMillis() % 100)
                        , new Position(0, 1)));
        messageSubscriber.onNext(new NewPlayerMessage()); //TODO: add to new player message player info.
    }

    public Publisher<WebSocketMessage> getGameFlow(WebSocketSession session) {
        return messageSubscriber
                .getOutputEvents()
                .map(mapper::toJSON)
                .map(session::textMessage);
    }

    private Mono<WebSocketMessage> handleBombPlacement(UserMessage message, WebSocketSession session) {
        GamePlayer player = message.getGamePlayer();
        int bombsLeft = player.getBombsLeft();
        if (bombsLeft < 0) {
            return Mono.empty();
        }
        player.setBombsLeft(bombsLeft - 1);
        return Mono.just(message)
                .delayElement(Duration.ofSeconds(EXPLOSION_DELAY * player.getBombsDelayMultiplier()))
                .map(value -> mapper.toJSON(value))
                .map(session::textMessage);
    }

    public static void main(String[] args) {
        final int[] count = {0};

        Flux.just("a", "b", "c", "m", "l",
                "b", "q", "m", "l", "ready",
                "b", "q", "m", "l", "m", "l",
                "b", "q", "m", "l", "ready",
                "m", "n", "b", "q", "m", "l",
                "u", "p", "s", "w")
                .delayElements(Duration.ofMillis(250))
                .skipUntil(x -> {
                    if (x.equals("ready")) {
                        count[0]++;
                        return false;
                    }
                    return count[0] == 2;
                })
                .log()
                .subscribe();

        Flux.just("start counter").log()
                .mergeWith(Mono.just("start game")
                        .delayElement(Duration.ofSeconds(3)).log())
                .subscribe();

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
