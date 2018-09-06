package eleks.mentorship.bigbang.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.user.UserMessage;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsonMessageMapper {
    private ObjectMapper mapper;

    public JsonMessageMapper() {
        mapper = new ObjectMapper();
    }

    public UserMessage toUserMessage(String json) {
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
