package eleks.mentorship.bigbang.gameplay;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eleks.mentorship.bigbang.domain.Position;
import lombok.AllArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
public class GamePlayer {
    private final PlayerInfo playerInfo;
    private final Integer livesLeft;
    private final Integer bombsLeft;
    private final Position position;
    @JsonIgnore
    private final Instant lastMoveTime;

    public GamePlayer(PlayerInfo playerInfo, Position startPosition, Instant lastMoveTime) {
        this.position = startPosition;
        this.playerInfo = playerInfo;
        this.livesLeft = 3;
        this.bombsLeft = 5;
        this.lastMoveTime = lastMoveTime;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public Integer getLivesLeft() {
        return livesLeft;
    }

    public Integer getBombsLeft() {
        return bombsLeft;
    }

    public Position getPosition() {
        return position;
    }

    public Instant getLastMoveTime() {
        return lastMoveTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GamePlayer that = (GamePlayer) o;

        if (playerInfo != null ? !playerInfo.equals(that.playerInfo) : that.playerInfo != null) return false;
        if (!livesLeft.equals(that.livesLeft)) return false;
        return bombsLeft.equals(that.bombsLeft);
    }

    @Override
    public int hashCode() {
        int result = playerInfo != null ? playerInfo.hashCode() : 0;
        result = 31 * result + livesLeft.hashCode();
        result = 31 * result + bombsLeft.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GamePlayer{" +
                "playerInfo=" + playerInfo +
                ", livesLeft=" + livesLeft +
                ", bombsLeft=" + bombsLeft +
                ", position=" + position +
                ", lastMoveTime=" + lastMoveTime +
                '}';
    }
}
