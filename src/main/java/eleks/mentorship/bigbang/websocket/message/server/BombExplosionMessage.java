package eleks.mentorship.bigbang.websocket.message.server;

import eleks.mentorship.bigbang.domain.Position;
import eleks.mentorship.bigbang.gameplay.GamePlayer;
import eleks.mentorship.bigbang.gameplay.field.ExplosionRange;
import eleks.mentorship.bigbang.websocket.message.MessageType;

import java.util.List;

import static eleks.mentorship.bigbang.websocket.message.MessageType.EXPLOSION;

public class BombExplosionMessage extends GameState {
    private final GamePlayer owner;
    private final Position position;
    private List<GamePlayer> damaged;
    private final ExplosionRange explosionRange;

    public BombExplosionMessage(GameState gameState, GamePlayer owner, Position position, ExplosionRange explosionRange) {
        super(gameState.getPlayers(), gameState.getGameField());
        this.owner = owner;
        this.position = position;
        this.explosionRange = explosionRange;
    }

    public GamePlayer getOwner() {
        return owner;
    }

    public Position getPosition() {
        return position;
    }

    public List<GamePlayer> getDamaged() {
        return damaged;
    }

    public ExplosionRange getExplosionRange() {
        return explosionRange;
    }

    public void setDamaged(List<GamePlayer> damaged) {
        this.damaged = damaged;
    }

    @Override
    public MessageType getType() {
        return EXPLOSION;
    }
}
