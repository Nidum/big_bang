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
}
