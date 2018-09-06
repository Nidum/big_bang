package eleks.mentorship.bigbang.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SystemConfig {
    private static Environment env;

    public long getTimePrecision() {
        return env.getProperty("gameplay.time-precision", Long.class);
    }
}
