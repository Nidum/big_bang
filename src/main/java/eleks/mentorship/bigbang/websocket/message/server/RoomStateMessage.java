package eleks.mentorship.bigbang.websocket.message.server;

import eleks.mentorship.bigbang.Player;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * Created by Emiliia Nesterovych on 8/30/2018.
 */
@Data
@AllArgsConstructor
public class RoomStateMessage extends GameMessage {
    private Map<Player, Boolean> playerList;
}
