package eleks.mentorship.bigbang.websocket.message;

import eleks.mentorship.bigbang.Player;
import eleks.mentorship.bigbang.gameplay.GamePlayer;
import eleks.mentorship.bigbang.util.Position;
import lombok.Data;

/**
 * Created by Emiliia Nesterovych on 8/21/2018.
 */
@Data
public abstract class UserMessage extends GameMessage {
    protected Player player;
}
