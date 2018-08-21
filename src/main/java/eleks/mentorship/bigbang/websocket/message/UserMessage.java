package eleks.mentorship.bigbang.websocket.message;

import eleks.mentorship.bigbang.gameplay.GamePlayer;
import lombok.Data;

/**
 * Created by Emiliia Nesterovych on 8/21/2018.
 */
@Data
public class UserMessage extends GameMessage {
    protected GamePlayer gamePlayer;
}
