package eleks.mentorship.bigbang.websocket.message.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eleks.mentorship.bigbang.gameplay.GameField;
import eleks.mentorship.bigbang.gameplay.GamePlayer;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static eleks.mentorship.bigbang.websocket.message.MessageType.GAME_STATE;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameState extends GameMessage {
    @JsonIgnore
    private Map<String, Pair<GamePlayer, LocalDateTime>> playersMovesTime;
    private List<GamePlayer> players;
    private GameField gameField;

    @Override
    public MessageType getType() {
        return GAME_STATE;
    }
}
