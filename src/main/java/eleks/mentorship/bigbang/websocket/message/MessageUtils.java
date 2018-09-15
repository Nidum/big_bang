package eleks.mentorship.bigbang.websocket.message;

import java.util.function.Predicate;

import static eleks.mentorship.bigbang.websocket.message.MessageType.PLAYER_MOVE;
import static eleks.mentorship.bigbang.websocket.message.MessageType.PLAYER_PLACE_BOMB;

public class MessageUtils {
    private MessageUtils(){}

    public static final Predicate<GameMessage> IS_POSITIONING_MESSAGE = message ->
            message.getType().equals(PLAYER_MOVE) ||
                    message.getType().equals(PLAYER_PLACE_BOMB);
}
