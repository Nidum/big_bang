package eleks.mentorship.bigbang.websocket.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import eleks.mentorship.bigbang.websocket.message.server.*;
import eleks.mentorship.bigbang.websocket.message.user.BombPlacementMessage;
import eleks.mentorship.bigbang.websocket.message.user.ConnectMessage;
import eleks.mentorship.bigbang.websocket.message.user.MoveMessage;
import eleks.mentorship.bigbang.websocket.message.user.ReadyMessage;
import lombok.Data;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GameStartMessage.class, name = "start"),
        @JsonSubTypes.Type(value = ReadyMessage.class, name = "ready"),
        @JsonSubTypes.Type(value = MoveMessage.class, name = "move"),
        @JsonSubTypes.Type(value = BombPlacementMessage.class, name = "bomb"),
        @JsonSubTypes.Type(value = GameState.class, name = "state"),
        @JsonSubTypes.Type(value = BombExplosionMessage.class, name = "explosion"),
        @JsonSubTypes.Type(value = ConnectMessage.class, name = "connect"),
        @JsonSubTypes.Type(value = RoomStateMessage.class, name = "room"),
        @JsonSubTypes.Type(value = StartCounterMessage.class, name = "start_counter"),
})
@Data
public abstract class GameMessage {
    protected Instant occurrence;

    @JsonProperty("type")
    public abstract MessageType getType();
}
