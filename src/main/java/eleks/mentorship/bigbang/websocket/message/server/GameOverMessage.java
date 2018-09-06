package eleks.mentorship.bigbang.websocket.message.server;

import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.MessageType;

import static eleks.mentorship.bigbang.websocket.message.MessageType.GAME_OVER;

public class GameOverMessage extends GameMessage {

    @Override
    public MessageType getType() {
        return GAME_OVER;
    }
}
