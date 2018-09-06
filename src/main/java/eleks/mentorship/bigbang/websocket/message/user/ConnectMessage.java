package eleks.mentorship.bigbang.websocket.message.user;

import eleks.mentorship.bigbang.websocket.message.MessageType;

import static eleks.mentorship.bigbang.websocket.message.MessageType.PLAYER_CONNECTED;

public class ConnectMessage extends UserMessage {
    @Override
    public MessageType getType() {
        return PLAYER_CONNECTED;
    }
}
