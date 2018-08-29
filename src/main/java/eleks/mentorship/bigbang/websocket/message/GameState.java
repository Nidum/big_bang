package eleks.mentorship.bigbang.websocket.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eleks.mentorship.bigbang.gameplay.GameField;
import eleks.mentorship.bigbang.gameplay.GamePlayer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Created by Emiliia Nesterovych on 7/1/2018.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameState extends GameMessage {
    @JsonIgnore
    private Map<String, Pair<GamePlayer, LocalDateTime>> playersMovesTime;
    private List<GamePlayer> players;
    private GameField gameField;
}
