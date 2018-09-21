package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.domain.Position;
import eleks.mentorship.bigbang.exception.MessageFromUnknownUserException;
import eleks.mentorship.bigbang.gameplay.GamePlayer;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.server.BombExplosionMessage;
import eleks.mentorship.bigbang.websocket.message.server.GameState;
import eleks.mentorship.bigbang.websocket.message.user.PositioningMessage;
import eleks.mentorship.bigbang.websocket.message.user.UserMessage;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static eleks.mentorship.bigbang.websocket.message.MessageType.BOMB;
import static eleks.mentorship.bigbang.websocket.message.MessageType.MOVE;

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
    public Flux<GameMessage> aggregate(List<PositioningMessage> messages, GameState oldState) {
        messages.sort(Comparator.comparing(UserMessage::getOccurrence));
        Flux<GameMessage> result = Flux.empty();

        for (PositioningMessage message : messages) {
            GamePlayer messageOwner = oldState
                    .getPlayers()
                    .stream()
                    .filter(gamePlayer -> gamePlayer.getPlayerInfo().equals(message.getPlayerInfo()))
                    .findFirst()
                    .orElseThrow(() -> new MessageFromUnknownUserException("Got message from unknown user: " + message));

            if (message.getType().equals(MOVE)) {
                Instant lastPlayersMove = messageOwner.getLastMoveTime();
                long timeBetween = ChronoUnit.MILLIS.between(lastPlayersMove, message.getOccurrence());

                if (timeBetween >= MOVE_DELTA && isCellAvailable(message, messageOwner, oldState)) {
                    Set<GamePlayer> actualPlayers = oldState.getPlayers()
                            .stream()
                            .filter(p -> !p.equals(messageOwner))
                            .collect(Collectors.toSet());

                    GamePlayer newPlayer = new GamePlayer(
                            messageOwner.getPlayerInfo(),
                            messageOwner.getLivesLeft(),
                            messageOwner.getBombsLeft(),
                            message.getPosition(),
                            Instant.now()
                    );
                    actualPlayers.add(newPlayer);

                    GameState newState = new GameState(actualPlayers, oldState.getGameField());
                    result = result.concatWith(Mono.just(newState));
                }
            } else if (message.getType().equals(BOMB)) {
                if (isCellAvailable(message, messageOwner, oldState) &&
                        !isPlayerOnCell(message, messageOwner, oldState) &&
                        messageOwner.getBombsLeft() > 0) {
                    Set<GamePlayer> actualPlayers = oldState.getPlayers()
                            .stream()
                            .filter(p -> !p.equals(messageOwner))
                            .collect(Collectors.toSet());

                    GamePlayer newPlayer = new GamePlayer(
                            messageOwner.getPlayerInfo(),
                            messageOwner.getLivesLeft(),
                            messageOwner.getBombsLeft() - 1,
                            message.getPosition(),
                            Instant.now()
                    );
                    actualPlayers.add(newPlayer);
                    GameState newState = new GameState(actualPlayers, oldState.getGameField());

                    Position position = message.getPosition();
                    newState.getGameField().getBombs().get(position.getX()).set(position.getY(), true);
                    BombExplosionMessage explosionMessage = new BombExplosionMessage(newState, newPlayer, position);

                    Flux<GameMessage> flux = Flux.just(newState);
                    result = result
                            .concatWith(flux.mergeWith(Mono.just(explosionMessage)
                                    .delayElement(Duration.ofSeconds(EXPLOSION_DELAY))));
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
        return !oldState.getGameField().getBombs().get(newPosition.getX()).get(newPosition.getY());
    }

    private boolean isPlayerOnCell(PositioningMessage message, GamePlayer player, GameState oldState) {
        return oldState.getPlayers()
                .stream()
                .filter(p -> !p.getPlayerInfo().equals(player.getPlayerInfo()))
                .map(GamePlayer::getPosition)
                .anyMatch(p -> p.equals(message.getPosition()));
    }
}
