package eleks.mentorship.bigbang.websocket.message.server;

import eleks.mentorship.bigbang.domain.PlayerReady;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

import static eleks.mentorship.bigbang.websocket.message.MessageType.ROOM;

@Data
@AllArgsConstructor
public class RoomStateMessage extends GameMessage {
    private List<PlayerReady> playersReady;

    @Override
    public MessageType getType() {
        return ROOM;
    }
}
