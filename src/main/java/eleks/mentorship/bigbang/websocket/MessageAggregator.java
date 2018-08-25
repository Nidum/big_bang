package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.common.exception.UnsupportedMessageTypeException;
import eleks.mentorship.bigbang.gameplay.GamePlayer;
import eleks.mentorship.bigbang.websocket.message.*;
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
    public GameMessage aggregate(List<UserMessage> messages, GameState oldState, EUserMessageType type) {
        switch (type){
            case MOVE:
                return aggregateMoves(messages, oldState);
            case BOMB:
                return aggregateBombPlacement(messages, oldState);
                default:
                    throw new UnsupportedMessageTypeException();
        }
    }

    private GameMessage aggregateBombPlacement(List<UserMessage> messages, GameState oldState){
        // TODO: Bomb explosion messages.
        System.out.println("Aggregating bombs");
        return new BombExplosionMessage();
    }

    private GameMessage aggregateMoves(List<UserMessage> messages, GameState oldState){
        // TODO: Aggregated moves.
        System.out.println("Aggregating moves");
        GamePlayer gamePlayer = oldState.getPlayers().get(0);
        Integer x = gamePlayer.getPosition().getX();
        gamePlayer.getPosition().setX(x + 1);
        return oldState;
    }

}
