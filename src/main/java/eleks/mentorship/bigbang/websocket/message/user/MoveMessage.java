package eleks.mentorship.bigbang.websocket.message.user;

import eleks.mentorship.bigbang.websocket.message.MessageType;

import static eleks.mentorship.bigbang.websocket.message.MessageType.PLAYER_MOVE;

public class MoveMessage extends PositioningMessage {
    @Override
    public MessageType getType() {
        return PLAYER_MOVE;
    }
}
