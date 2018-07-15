package eleks.mentorship.bigbang.gameplay;

import eleks.mentorship.bigbang.websocket.message.PositionMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Created by Emiliia Nesterovych on 7/1/2018.
 */
@Data
@AllArgsConstructor
public class GameState {
    private List<GamePlayer> players;
    private GameField gameField;

    /**
     * Aggregates messages into single game state.
     *
     * @param messages Messages to be agregated
     * @return Current game state.
     */
    public GameState produceGameState(List<PositionMessage> messages){

        return null;
    }
}
