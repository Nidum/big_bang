package eleks.mentorship.bigbang.websocket.message;

import eleks.mentorship.bigbang.gameplay.GamePlayer;
import eleks.mentorship.bigbang.util.Position;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Emiliia Nesterovych on 7/8/2018.
 */
@AllArgsConstructor
@Data
public class Message {
    private GamePlayer player;
    private Position position;
    private MessageType type;
}
