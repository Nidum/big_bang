package eleks.mentorship.bigbang.gameplay;

import eleks.mentorship.bigbang.websocket.message.Message;
import reactor.core.publisher.Mono;

/**
 * Created by Emiliia Nesterovych on 7/10/2018.
 */
public class GameEngine {
    public Mono<Void> handle(Message message) {
        switch (message.getType()) {
            case MOVE:
                handleMove(message);
                break;
            case BOMB_PLACEMENT:
                handleBombPlacement(message);
                break;
        }
        return Mono.empty();
    }

    private void handleMove(Message message) {

    }

    private void handleBombPlacement(Message message) {
        GamePlayer player = message.getPlayer();
        int bombsLeft = player.getBombsLeft();
        if (bombsLeft < 0) {

        }
        player.setBombsLeft(bombsLeft - 1);

    }
}
