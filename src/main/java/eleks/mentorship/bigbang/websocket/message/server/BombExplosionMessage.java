package eleks.mentorship.bigbang.websocket.message.server;

import eleks.mentorship.bigbang.domain.Position;
import eleks.mentorship.bigbang.gameplay.GamePlayer;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.MessageType;
import lombok.Data;

import java.util.List;

import static eleks.mentorship.bigbang.websocket.message.MessageType.EXPLOSION;

@Data
public class BombExplosionMessage extends GameMessage {
    protected GamePlayer owner;
    protected Position position;
    List<GamePlayer> damaged;

    public BombExplosionMessage(GamePlayer owner, Position position) {
        this.owner = owner;
        this.position = position;
    }

    @Override
    public MessageType getType() {
        return EXPLOSION;
    }
}
