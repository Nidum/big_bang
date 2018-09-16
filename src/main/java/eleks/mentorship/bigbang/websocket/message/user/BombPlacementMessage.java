package eleks.mentorship.bigbang.websocket.message.user;

import eleks.mentorship.bigbang.websocket.message.MessageType;

import static eleks.mentorship.bigbang.websocket.message.MessageType.BOMB;

public class BombPlacementMessage extends PositioningMessage {
    @Override
    public MessageType getType() {
        return BOMB;
    }
}
