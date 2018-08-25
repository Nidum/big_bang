package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.common.exception.UnsupportedMessageTypeException;
import eleks.mentorship.bigbang.websocket.message.BombPlacementMessage;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.MoveMessage;

/**
 * Created by Emiliia Nesterovych on 8/25/2018.
 */
public enum EUserMessageType {
    MOVE(MoveMessage.class),
    BOMB(BombPlacementMessage.class);

    private Class<? extends GameMessage> type;

    EUserMessageType(Class<? extends GameMessage> type) {
        this.type = type;
    }

    public Class<? extends GameMessage> getClazz() {
        return type;
    }

    public static EUserMessageType findByClass(Class<? extends GameMessage> key) {
        for (EUserMessageType messageType : values()) {
            if (messageType.getClazz().equals(key)) {
                return messageType;
            }
        }
        throw new UnsupportedMessageTypeException();
    }
}
