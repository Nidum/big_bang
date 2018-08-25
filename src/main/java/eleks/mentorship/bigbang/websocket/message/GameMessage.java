package eleks.mentorship.bigbang.websocket.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import eleks.mentorship.bigbang.Player;
import eleks.mentorship.bigbang.gameplay.GamePlayer;
import eleks.mentorship.bigbang.mapper.JsonMessageMapper;
import eleks.mentorship.bigbang.util.Position;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.UUID;

/**
 * Created by Emiliia Nesterovych on 8/21/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GameStartMessage.class, name = "start"),
        @JsonSubTypes.Type(value = ReadyMessage.class, name = "ready"),
        @JsonSubTypes.Type(value = MoveMessage.class, name = "move"),
        @JsonSubTypes.Type(value = BombPlacementMessage.class, name = "bomb")
})
public interface GameMessage {
    public static void main(String[] args) {
        int count[] = new int[1];
        Flux.just(0, 1, 2, 3, 5, 6, 7, 8, 9, 10, 11, 5, 3, 2, 1)
                .delayElements(Duration.ofMillis(250))
                .mergeWith(Flux.just(4, 4, 4, 4, 4).delaySubscription(Duration.ofSeconds(3)))
                .log()
                .subscribe();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
