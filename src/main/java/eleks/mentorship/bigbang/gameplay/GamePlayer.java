package eleks.mentorship.bigbang.gameplay;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eleks.mentorship.bigbang.domain.Position;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class GamePlayer {
    private final PlayerInfo playerInfo;
    private Integer livesLeft = 3;
    private Integer bombsLeft = 5;
    private Integer bombsDelayMultiplier = 1;
    private Position position;
    @JsonIgnore
    private Instant lastMoveTime;

    public GamePlayer(PlayerInfo playerInfo) {
        this(playerInfo, new Position(0,0));
    }

    public GamePlayer(PlayerInfo playerInfo, Position startPosition) {
        this.position = startPosition;
        this.playerInfo = playerInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GamePlayer that = (GamePlayer) o;

        if (playerInfo != null ? !playerInfo.equals(that.playerInfo) : that.playerInfo != null) return false;
        if (livesLeft != null ? !livesLeft.equals(that.livesLeft) : that.livesLeft != null) return false;
        if (bombsLeft != null ? !bombsLeft.equals(that.bombsLeft) : that.bombsLeft != null) return false;
        return bombsDelayMultiplier != null ? bombsDelayMultiplier.equals(that.bombsDelayMultiplier) : that.bombsDelayMultiplier == null;
    }

    @Override
    public int hashCode() {
        int result = (playerInfo != null ? playerInfo.hashCode() : 0);
        result = 31 * result + (livesLeft != null ? livesLeft.hashCode() : 0);
        result = 31 * result + (bombsLeft != null ? bombsLeft.hashCode() : 0);
        result = 31 * result + (bombsDelayMultiplier != null ? bombsDelayMultiplier.hashCode() : 0);
        return result;
    }
}
