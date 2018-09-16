package eleks.mentorship.bigbang.websocket.message.user;

import eleks.mentorship.bigbang.websocket.message.MessageType;

import static eleks.mentorship.bigbang.websocket.message.MessageType.MOVE;

public class MoveMessage extends PositioningMessage {
    @Override
    public MessageType getType() {
        return MOVE;
    }
}
