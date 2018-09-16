package eleks.mentorship.bigbang.websocket.message.server;

import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.MessageType;

import static eleks.mentorship.bigbang.websocket.message.MessageType.START_COUNTER;

public class StartCounterMessage extends GameMessage {
    @Override
    public MessageType getType() {
        return START_COUNTER;
    }
}
