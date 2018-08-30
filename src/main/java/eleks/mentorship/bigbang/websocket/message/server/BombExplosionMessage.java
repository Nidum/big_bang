package eleks.mentorship.bigbang.websocket.message.server;

import eleks.mentorship.bigbang.gameplay.GamePlayer;
import eleks.mentorship.bigbang.util.Position;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import lombok.Data;

import java.util.List;

/**
 * Created by Emiliia Nesterovych on 8/25/2018.
 */
@Data
public class BombExplosionMessage extends GameMessage {
    protected GamePlayer owner;
    protected Position position;
    List<GamePlayer> damaged;

    public BombExplosionMessage(GamePlayer owner, Position position) {
        this.owner = owner;
        this.position = position;
    }
}
