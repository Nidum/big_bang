package eleks.mentorship.bigbang.websocket.message.server;

import eleks.mentorship.bigbang.gameplay.PlayerInfo;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static eleks.mentorship.bigbang.websocket.message.MessageType.GAME_OVER;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GameOverMessage extends GameMessage {
    private PlayerInfo winner;

    @Override
    public MessageType getType() {
        return GAME_OVER;
    }
}
