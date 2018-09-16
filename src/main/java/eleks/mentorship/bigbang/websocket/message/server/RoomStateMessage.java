package eleks.mentorship.bigbang.websocket.message.server;

import eleks.mentorship.bigbang.gameplay.PlayerInfo;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

import static eleks.mentorship.bigbang.websocket.message.MessageType.ROOM;

@Data
@AllArgsConstructor
public class RoomStateMessage extends GameMessage {
    private Map<PlayerInfo, Boolean> playerList;

    @Override
    public MessageType getType() {
        return ROOM;
    }
}
