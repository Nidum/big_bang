package eleks.mentorship.bigbang.websocket.message.server;

import eleks.mentorship.bigbang.domain.Position;
import eleks.mentorship.bigbang.exception.TooMuchPlayersInRoomException;
import eleks.mentorship.bigbang.gameplay.GameField;
import eleks.mentorship.bigbang.gameplay.GamePlayer;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

import static eleks.mentorship.bigbang.websocket.message.MessageType.STATE;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameState extends GameMessage {
    private Set<GamePlayer> players;
    private GameField gameField;

    @Override
    public MessageType getType() {
        return STATE;
    }

    public Position getFreeSpawn() {
        return gameField.getSpawns()
                .stream()
                .filter(spawn -> players
                        .stream()
                        .noneMatch(p -> p.getPosition().equals(spawn)))
                .findFirst()
                .orElseThrow(TooMuchPlayersInRoomException::new);
    }
}
