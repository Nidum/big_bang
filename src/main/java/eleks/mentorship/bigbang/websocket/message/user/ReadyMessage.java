package eleks.mentorship.bigbang.websocket.message.user;

import eleks.mentorship.bigbang.websocket.message.MessageType;

import static eleks.mentorship.bigbang.websocket.message.MessageType.READY;

public class ReadyMessage extends UserMessage {
    @Override
    public MessageType getType() {
        return READY;
    }
}
