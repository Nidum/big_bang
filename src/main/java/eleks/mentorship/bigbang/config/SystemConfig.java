package eleks.mentorship.bigbang.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SystemConfig {
    @Autowired
    private Environment env;

    public long getTimePrecision() {
        return env.getProperty("gameplay.time-precision", Long.class);
    }

    public String getFrontEndLocation() {
        return env.getProperty("location.front-end", String.class);
    }


}
