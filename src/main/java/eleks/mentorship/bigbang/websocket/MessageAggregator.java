package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.gameplay.GamePlayer;
import eleks.mentorship.bigbang.util.Position;
import eleks.mentorship.bigbang.websocket.message.*;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by Emiliia Nesterovych on 8/24/2018.
 */
@Component
public class MessageAggregator {
    private static final long MOVE_DELTA = 1000; // In milliseconds.
    private static final long EXPLOSION_DELAY = 5; // In seconds.

    /**
     * Aggregates messages into single game state.
     * Note: this method updates old game state passed as parameter to recent.
     *
     * @param messages Messages to be aggregated.
     * @return Current game state.
     */
    public Flux<GameMessage> aggregate(List<UserMessage> messages, GameState oldState) {
        messages.sort(Comparator.comparing(UserMessage::getOccurence));
        Map<String, Pair<GamePlayer, LocalDateTime>> playersMovesTime = oldState.getPlayersMovesTime();

        Flux<GameMessage> result = Flux.empty();

        for (UserMessage message : messages) {
            String nickname = message.getGamePlayer().getPlayer().getNickname();
            Pair<GamePlayer, LocalDateTime> pair = playersMovesTime.get(nickname);
            GamePlayer player = pair.getLeft();

            if (message instanceof MoveMessage) {
                LocalDateTime lastPlayersMove = pair.getRight();
                long timeBetween = ChronoUnit.MILLIS.between(lastPlayersMove, message.getOccurence());
                if (timeBetween >= MOVE_DELTA && isCellAvailable(message, player, oldState)) {
                    pair.setValue(message.getOccurence());
                    Position oldPosition = player.getPosition();
                    oldPosition.setX(message.getPosition().getX());
                    oldPosition.setY(message.getPosition().getY());
                    result = result.concatWith(Mono.just(oldState));
                }
            } else if (message instanceof BombPlacementMessage) {
                if (isCellAvailable(message, player, oldState) &&
                        !isPlayerOnCell(message, player, oldState) &&
                        player.getBombsLeft() > 0) {
                    player.setBombsLeft(player.getBombsLeft() - 1);
                    Position position = message.getPosition();
                    oldState.getGameField().getBombs().get(position.getX()).set(position.getY(), true);
                    BombExplosionMessage explosionMessage = new BombExplosionMessage(player, position);
                    Flux<GameMessage> flux = Flux.just(oldState);
                    result = result.concatWith(flux.mergeWith(Mono.just(explosionMessage).delayElement(Duration.ofSeconds(EXPLOSION_DELAY))));
                }
            }
        }
        return result;
    }

    /**
     * Makes following checks:
     * - if cell is in radius of 1 cell;
     * - if cell is inside of gamefield;
     * - if cell is free of bombs.
     *
     * @param message  Message with position to be checked.
     * @param player   Player for which this check should be done.
     * @param oldState Last known state of game.
     * @return True if all checks passed, false otherwise.
     */
    private boolean isCellAvailable(UserMessage message, GamePlayer player, GameState oldState) {
        Position oldPosition = player.getPosition();
        Position newPosition = message.getPosition();
        int xStepDelta = oldPosition.getX() - newPosition.getX();
        int yStepDelta = oldPosition.getY() - newPosition.getY();

        // Move can be done on 1 cell in one direction only.
        if (!((Math.abs(xStepDelta) == 1 ^ Math.abs(yStepDelta) == 1) ||
                (Math.abs(xStepDelta) == 0 && Math.abs(yStepDelta) == 0))) {
            return false;
        }

        // Check if cell is out of range
        int height = oldState.getGameField().getHeight();
        int width = oldState.getGameField().getWidth();
        if (newPosition.getX() >= width || newPosition.getX() < 0
                || newPosition.getY() >= height || newPosition.getY() < 0) {
            return false;
        }

        // Check if cell is free of bombs.
        return !oldState.getGameField().getBombs().get(newPosition.getX()).get(newPosition.getY());
    }

    private boolean isPlayerOnCell(UserMessage message, GamePlayer player, GameState oldState) {
        return oldState.getPlayers().stream()
                .filter(p -> !p.getPlayer().getNickname().equals(player.getPlayer().getNickname()))
                .map(GamePlayer::getPosition)
                .anyMatch(p -> p.equals(message.getPosition()));
    }
}
