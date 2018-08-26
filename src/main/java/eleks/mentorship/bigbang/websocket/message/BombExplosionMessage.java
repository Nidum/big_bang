package eleks.mentorship.bigbang.websocket.message;

import eleks.mentorship.bigbang.gameplay.GamePlayer;
import eleks.mentorship.bigbang.util.Position;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Emiliia Nesterovych on 8/25/2018.
 */
@Data
@AllArgsConstructor
public class BombExplosionMessage extends GameMessage {
    protected GamePlayer owner;
    protected Position position;
}
