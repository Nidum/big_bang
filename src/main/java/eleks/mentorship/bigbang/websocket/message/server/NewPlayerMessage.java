package eleks.mentorship.bigbang.websocket.message.server;

import eleks.mentorship.bigbang.Player;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
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
