package eleks.mentorship.bigbang.websocket.message.user;

import eleks.mentorship.bigbang.util.Position;
import lombok.Data;

/**
 * Created by Emiliia Nesterovych on 8/30/2018.
 */
@Data
public class PositioningMessage extends UserMessage {
    protected Position position;
}
