package eleks.mentorship.bigbang.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eleks.mentorship.bigbang.websocket.message.PositioningMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

/**
 * Created by Emiliia Nesterovych on 7/14/2018.
 */
@Configuration
public class BeanConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        return objectMapper;
    }

    @Bean
    public UnicastProcessor<PositioningMessage> eventPublisher(){
        return UnicastProcessor.create();
    }

    @Bean
    public Flux<PositioningMessage> events(UnicastProcessor<PositioningMessage> eventPublisher) {
        return eventPublisher
                .replay(25)
                .autoConnect();
    }
}
