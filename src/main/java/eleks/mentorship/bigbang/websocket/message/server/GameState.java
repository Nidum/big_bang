package eleks.mentorship.bigbang.websocket.message.server;

import eleks.mentorship.bigbang.domain.Position;
import eleks.mentorship.bigbang.exception.TooMuchPlayersInRoomException;
import eleks.mentorship.bigbang.gameplay.GameField;
import eleks.mentorship.bigbang.gameplay.GamePlayer;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.MessageType;
import lombok.NoArgsConstructor;

import java.util.Set;

import static eleks.mentorship.bigbang.websocket.message.MessageType.STATE;

@NoArgsConstructor
public class GameState extends GameMessage {
    private Set<GamePlayer> players;
    private GameField gameField;

    public GameState(Set<GamePlayer> players, GameField gameField) {
        this.players = players;
        this.gameField = gameField;
    }

    public Set<GamePlayer> getPlayers() {
        return players;
    }

    public void setPlayers(Set<GamePlayer> players) {
        this.players = players;
    }

    public GameField getGameField() {
        return gameField;
    }

    public void setGameField(GameField gameField) {
        this.gameField = gameField;
    }

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
