package eleks.mentorship.bigbang.websocket.message.server;

import eleks.mentorship.bigbang.domain.Position;
import eleks.mentorship.bigbang.gameplay.GamePlayer;
import eleks.mentorship.bigbang.websocket.message.MessageType;

import java.util.List;

import static eleks.mentorship.bigbang.websocket.message.MessageType.EXPLOSION;

public class BombExplosionMessage extends GameState {
    protected GamePlayer owner;
    protected Position position;
    List<GamePlayer> damaged;

    public BombExplosionMessage(GameState gameState, GamePlayer owner, Position position) {
        super(gameState.getPlayers(), gameState.getGameField());
        this.owner = owner;
        this.position = position;
    }

    public GamePlayer getOwner() {
        return owner;
    }

    public void setOwner(GamePlayer owner) {
        this.owner = owner;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public List<GamePlayer> getDamaged() {
        return damaged;
    }

    public void setDamaged(List<GamePlayer> damaged) {
        this.damaged = damaged;
    }

    @Override
    public MessageType getType() {
        return EXPLOSION;
    }
}
