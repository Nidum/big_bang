package eleks.mentorship.bigbang.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by Emiliia Nesterovych on 8/20/2018.
 */
@Component
public class JsonEventMapper {
    private ObjectMapper mapper;

    public JsonEventMapper() {
        mapper = new ObjectMapper();
    }

    public GameMessage toEvent(String json) {
        try {
            return mapper.readValue(json, GameMessage.class);
        } catch (IOException e) {
            throw new RuntimeException("Invalid JSON:" + json, e);
        }
    }

    public String toJSON(GameMessage event) {
        try {
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
