package eleks.mentorship.bigbang.websocket.message;

import eleks.mentorship.bigbang.Player;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created by Emiliia Nesterovych on 8/30/2018.
 */
public class RoomStateMessage extends GameMessage {
    private List<Pair<Player, Boolean>> playerList;
}
