package eleks.mentorship.bigbang.websocket.message;

import eleks.mentorship.bigbang.gameplay.GameField;
import eleks.mentorship.bigbang.gameplay.GamePlayer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Emiliia Nesterovych on 7/1/2018.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameState implements GameMessage {
    private List<GamePlayer> players;
    private GameField gameField;
}
