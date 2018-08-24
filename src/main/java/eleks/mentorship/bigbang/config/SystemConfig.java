package eleks.mentorship.bigbang.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by Emiliia Nesterovych on 8/24/2018.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SystemConfig {
    private static Environment env;

    public long getTimePrecision() {
        return env.getProperty("gameplay.time-precision", Long.class);
    }
}
