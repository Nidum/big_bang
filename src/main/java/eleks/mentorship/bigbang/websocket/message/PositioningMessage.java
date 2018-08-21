package eleks.mentorship.bigbang.websocket.message;

import eleks.mentorship.bigbang.gameplay.GamePlayer;
import eleks.mentorship.bigbang.util.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Emiliia Nesterovych on 7/8/2018.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PositioningMessage extends UserMessage {
    private Position position;
    private PositioningMessageType type;
}
