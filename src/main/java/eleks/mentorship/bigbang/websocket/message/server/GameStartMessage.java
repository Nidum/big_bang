package eleks.mentorship.bigbang.websocket.message.server;

import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.MessageType;

import static eleks.mentorship.bigbang.websocket.message.MessageType.GAME_START;

public class GameStartMessage extends GameMessage {

    @Override
    public MessageType getType() {
        return GAME_START;
    }
}
