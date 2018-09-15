package eleks.mentorship.bigbang.websocket.message.server;

import eleks.mentorship.bigbang.gameplay.GameField;
import eleks.mentorship.bigbang.gameplay.GamePlayer;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static eleks.mentorship.bigbang.websocket.message.MessageType.GAME_STATE;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameState extends GameMessage {
    private List<GamePlayer> players;
    private GameField gameField;

    @Override
    public MessageType getType() {
        return GAME_STATE;
    }
}
