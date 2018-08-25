package eleks.mentorship.bigbang.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.UserMessage;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by Emiliia Nesterovych on 8/20/2018.
 */
@Component
public class JsonMessageMapper {
    private ObjectMapper mapper;

    public JsonMessageMapper() {
        mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
    }

    public GameMessage toMessage(String json) {
        try {
            return mapper.readValue(json, UserMessage.class);
        } catch (IOException e) {
            throw new RuntimeException("Invalid JSON:" + json, e);
        }
    }

    public String toJSON(GameMessage message) {
        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
