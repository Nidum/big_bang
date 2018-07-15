package eleks.mentorship.bigbang.gameplay;

import eleks.mentorship.bigbang.Player;
import eleks.mentorship.bigbang.util.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Emiliia Nesterovych on 7/1/2018.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GamePlayer {
    private Player player;
    private Integer livesLeft = 3;
    private Integer bombsLeft = 5;
    private Position position = new Position();

    public GamePlayer(Player player) {
        this.player = player;
    }
}
