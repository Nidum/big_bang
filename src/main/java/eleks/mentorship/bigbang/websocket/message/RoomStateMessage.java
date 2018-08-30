package eleks.mentorship.bigbang.websocket.message;

import eleks.mentorship.bigbang.Player;
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
