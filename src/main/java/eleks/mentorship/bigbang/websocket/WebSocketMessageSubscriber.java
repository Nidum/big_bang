package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.common.exception.UserMissingException;
import eleks.mentorship.bigbang.gameplay.GamePlayer;
import eleks.mentorship.bigbang.util.Position;
import eleks.mentorship.bigbang.websocket.message.*;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Created by Emiliia Nesterovych on 8/24/2018.
 */
@Data
public class WebSocketMessageSubscriber {
    private UnicastProcessor<GameMessage> eventPublisher;
    private Flux<GameMessage> outputEvents;
    private MessageAggregator aggregator = new MessageAggregator();
    private GameState currentGameState;

    public WebSocketMessageSubscriber(GameState gameState) {
        this.eventPublisher = UnicastProcessor.create();
        this.currentGameState = gameState;
        // TODO: move it out of here.
        this.outputEvents = eventPublisher
                .replay()
                .autoConnect()
                .filter(x -> x instanceof UserMessage)
                .map(x -> {
                    x.setOccurrence(LocalDateTime.now());
                    return (UserMessage) x;
                })
                .buffer(Duration.ofSeconds(2))
                .flatMap(messages ->
                        aggregator.aggregate(messages, currentGameState))
                .doOnNext(msg -> {
                    if (msg instanceof BombExplosionMessage) {
                        BombExplosionMessage bombMsg = (BombExplosionMessage) msg;
                        GamePlayer gamePlayer = bombMsg.getOwner();
                        GamePlayer fieldPlayer = currentGameState.getPlayers().stream()
                                .filter(x -> x.getPlayer().getNickname().equals(gamePlayer.getPlayer().getNickname()))
                                .findFirst()
                                .orElseThrow(UserMissingException::new);
                        fieldPlayer.setBombsLeft(fieldPlayer.getBombsLeft() + 1);

                        Position bombPosition = bombMsg.getPosition();
                        // TODO: decrease HP of players in explosion range.

                        gameState.getPlayers().stream().map(GamePlayer::getPosition).collect(Collectors.toList());
                        gameState.getGameField().getBombs().get(bombPosition.getX()).set(bombPosition.getY(), false);
                    }
                });
        // TODO: respect explosion messages on game state
    }

    public void onNext(GameMessage event) {
        eventPublisher.onNext(event);
    }

    public void onError(Throwable error) {
        //TODO log error
        error.printStackTrace();
    }
}
