package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.domain.Position;
import eleks.mentorship.bigbang.exception.InvalidMessageException;
import eleks.mentorship.bigbang.exception.MessageFromUnknownUserException;
import eleks.mentorship.bigbang.gameplay.GamePlayer;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.server.BombExplosionMessage;
import eleks.mentorship.bigbang.websocket.message.server.GameState;
import eleks.mentorship.bigbang.websocket.message.user.PositioningMessage;
import org.springframework.stereotype.Component;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class MessageAggregator {
    private static final long MOVE_DELTA = 100; // In milliseconds.
    private static final long EXPLOSION_DELAY = 2; // In seconds.

    /**
     * Aggregates messages into single game state.
     *
     * @param messages Messages to be aggregated.
     * @return Current game state.
     */
    public GameMessage aggregate(List<PositioningMessage> messages, GameState oldState,
                                 FluxSink<BombExplosionMessage> bombConsumer) {

        GameState newState = new GameState(oldState);
        newState.cleanExplosions();

        for (PositioningMessage message : messages) {
            GamePlayer messageOwner = oldState
                    .getPlayers()
                    .stream()
                    .filter(gamePlayer -> gamePlayer.getPlayerInfo().equals(message.getPlayerInfo()))
                    .findFirst()
                    .orElseThrow(() -> new MessageFromUnknownUserException("Got message from unknown user: " + message));
            boolean isPlayerAlive = messageOwner.getLivesLeft() > 0;
            switch (message.getType()) {
                case MOVE:
                    Instant lastPlayersMove = messageOwner.getLastMoveTime();
                    long timeBetween = ChronoUnit.MILLIS.between(lastPlayersMove, message.getOccurrence());

                    if (timeBetween >= MOVE_DELTA &&
                            isCellAvailable(message, messageOwner, oldState) &&
                            isPlayerAlive) {
                        newState.movePlayer(message.getPosition(), message.getPlayerInfo());
                    }
                    break;
                case BOMB:
                    if (isCellAvailable(message, messageOwner, oldState) &&
                            messageOwner.getBombsLeft() > 0 &&
                            isPlayerAlive) {
                        BombExplosionMessage bombExplosionMessage = newState.placeBomb(message.getPosition(), messageOwner);
                        Mono.just(bombExplosionMessage)
                                .delaySubscription(Duration.ofSeconds(EXPLOSION_DELAY))
                                .log()
                                .subscribe(bombConsumer::next);
                    }
                    break;
                case EXPLOSION:
                    message.setOccurrence(Instant.now());
                    newState.explodeBomb((BombExplosionMessage) message);
                    break;
                default:
                    throw new InvalidMessageException();
            }

        }
        return newState;
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
    private boolean isCellAvailable(PositioningMessage message, GamePlayer player, GameState oldState) {
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
        return !oldState.getGameField().getBombs().get(newPosition.getY()).get(newPosition.getX()) &&
                // Is it plain field cell.
                oldState.getGameField().isCellAvailableForMove(newPosition);
    }

    private boolean isPlayerOnCell(PositioningMessage message, GamePlayer player, GameState oldState) {
        return oldState.getPlayers()
                .stream()
                .filter(p -> !p.getPlayerInfo().equals(player.getPlayerInfo()))
                .map(GamePlayer::getPosition)
                .anyMatch(p -> p.equals(message.getPosition()));
    }
}
