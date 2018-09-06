package eleks.mentorship.bigbang.websocket.message.user;

import eleks.mentorship.bigbang.domain.Position;
import lombok.Data;

@Data
public abstract class PositioningMessage extends UserMessage {
    protected Position position;
}
