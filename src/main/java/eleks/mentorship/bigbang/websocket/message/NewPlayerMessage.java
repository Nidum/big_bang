package eleks.mentorship.bigbang.websocket.message;

import eleks.mentorship.bigbang.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Emiliia Nesterovych on 8/25/2018.
 */
@Data
@AllArgsConstructor
public class NewPlayerMessage extends GameMessage {
    private Player player;
}
