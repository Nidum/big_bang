package eleks.mentorship.bigbang.websocket.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Created by Emiliia Nesterovych on 8/21/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GameStartMessage.class, name = "start"),
        @JsonSubTypes.Type(value = ReadyMessage.class, name = "ready"),
        @JsonSubTypes.Type(value = MoveMessage.class, name = "move"),
        @JsonSubTypes.Type(value = BombPlacementMessage.class, name = "bomb"),
        @JsonSubTypes.Type(value = GameState.class, name = "state"),
        @JsonSubTypes.Type(value = BombExplosionMessage.class, name = "explosion"),
})
@Data
public abstract class GameMessage {
    protected LocalDateTime occurrence;
}
