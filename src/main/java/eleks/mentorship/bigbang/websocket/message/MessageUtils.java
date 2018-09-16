package eleks.mentorship.bigbang.websocket.message;

import java.util.function.Predicate;

import static eleks.mentorship.bigbang.websocket.message.MessageType.MOVE;
import static eleks.mentorship.bigbang.websocket.message.MessageType.BOMB;

public class MessageUtils {
    private MessageUtils(){}

    public static final Predicate<GameMessage> IS_POSITIONING_MESSAGE = message ->
            message.getType().equals(MOVE) ||
                    message.getType().equals(BOMB);
}
