package eleks.mentorship.bigbang.websocket.message.user;

import eleks.mentorship.bigbang.Player;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import lombok.Data;

/**
 * Created by Emiliia Nesterovych on 8/21/2018.
 */
@Data
public abstract class UserMessage extends GameMessage {
    protected Player player;
}
