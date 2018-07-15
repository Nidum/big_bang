package eleks.mentorship.bigbang.websocket.message;

import eleks.mentorship.bigbang.gameplay.GamePlayer;
import eleks.mentorship.bigbang.util.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Emiliia Nesterovych on 7/8/2018.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PositionMessage {
    private GamePlayer gamePlayer;
    private Position position;
    private MessageType type;
}
