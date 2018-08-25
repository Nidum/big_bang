package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.gameplay.GamePlayer;
import eleks.mentorship.bigbang.websocket.message.GameState;
import eleks.mentorship.bigbang.websocket.message.UserMessage;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Emiliia Nesterovych on 8/24/2018.
 */
@Component
public class MessageAggregator {
    /**
     * Aggregates messages into single game state.
     *
     * @param messages Messages to be aggregated.
     * @return Current game state.
     */
    public GameState aggregate(List<UserMessage> messages, GameState oldState) {
        //TODO: transform game state.
        GamePlayer gamePlayer = oldState.getPlayers().get(0);
        Integer x = gamePlayer.getPosition().getX();
        gamePlayer.getPosition().setX(x + 1);
        return oldState;
    }
}
