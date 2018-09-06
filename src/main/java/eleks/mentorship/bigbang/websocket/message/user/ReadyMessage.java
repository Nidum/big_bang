package eleks.mentorship.bigbang.websocket.message.user;

import eleks.mentorship.bigbang.websocket.message.MessageType;

import static eleks.mentorship.bigbang.websocket.message.MessageType.PLAYER_READY;

public class ReadyMessage extends UserMessage {
    @Override
    public MessageType getType() {
        return PLAYER_READY;
    }
}
