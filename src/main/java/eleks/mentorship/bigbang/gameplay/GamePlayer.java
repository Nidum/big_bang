package eleks.mentorship.bigbang.gameplay;

import eleks.mentorship.bigbang.Player;
import eleks.mentorship.bigbang.domain.Position;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
//@NoArgsConstructor
@AllArgsConstructor
public class GamePlayer {
    private Player player;
    private Integer livesLeft = 3;
    private Integer bombsLeft = 5;
    private Integer bombsDelayMultiplier = 1;
    private Position position;

    public GamePlayer(Player player) {
        this.player = player;
        position = new Position(0, 0);
    }

    public GamePlayer(Player player, Position startPosition) {
        this.player = player;
        position = startPosition;
    }
}
