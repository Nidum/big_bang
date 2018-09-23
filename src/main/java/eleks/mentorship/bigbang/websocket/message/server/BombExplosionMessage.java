package eleks.mentorship.bigbang.websocket.message.server;

import eleks.mentorship.bigbang.domain.Position;
import eleks.mentorship.bigbang.gameplay.PlayerInfo;
import eleks.mentorship.bigbang.gameplay.field.ExplosionRange;
import eleks.mentorship.bigbang.websocket.message.MessageType;
import eleks.mentorship.bigbang.websocket.message.user.PositioningMessage;

import static eleks.mentorship.bigbang.websocket.message.MessageType.EXPLOSION;

public class BombExplosionMessage extends PositioningMessage {
    private final ExplosionRange explosionRange;

    public BombExplosionMessage(PlayerInfo owner, Position position, ExplosionRange explosionRange) {
        this.playerInfo = owner;
        this.position = position;
        this.explosionRange = explosionRange;
    }

    public Position getPosition() {
        return position;
    }

    public ExplosionRange getExplosionRange() {
        return explosionRange;
    }

    @Override
    public MessageType getType() {
        return EXPLOSION;
    }
}
