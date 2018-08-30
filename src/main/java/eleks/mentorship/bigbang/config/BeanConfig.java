package eleks.mentorship.bigbang.config;

import eleks.mentorship.bigbang.websocket.message.user.UserMessage;
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
    public UnicastProcessor<UserMessage> eventPublisher(){
        return UnicastProcessor.create();
    }

    @Bean
    public Flux<UserMessage> events(UnicastProcessor<UserMessage> eventPublisher) {
        return eventPublisher
                .replay(25)
                .autoConnect();
    }
}
