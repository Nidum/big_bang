package eleks.mentorship.bigbang.websocket.message.server;

import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

import static eleks.mentorship.bigbang.websocket.message.MessageType.ROOM_STATE;

@Data
@AllArgsConstructor
public class RoomStateMessage extends GameMessage {
    private Map<String, Boolean> playerList;

    @Override
    public MessageType getType() {
        return ROOM_STATE;
    }
}
