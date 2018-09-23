package eleks.mentorship.bigbang.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {

    @Autowired
    private SystemConfig config;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        RedirectServerAuthenticationSuccessHandler redirectHandler = new RedirectServerAuthenticationSuccessHandler(config.getFrontEndLocation());
        redirectHandler.setRequestCache(NoOpServerRequestCache.getInstance());
        return http
                .authorizeExchange()
                .anyExchange()
                .authenticated()
                .and()
                .formLogin()
                .authenticationSuccessHandler(redirectHandler)
                .and()
                .build();
    }

    @Bean
    public ServerLogoutSuccessHandler logoutSuccessHandler() {
        RedirectServerLogoutSuccessHandler logoutSuccessHandler = new RedirectServerLogoutSuccessHandler();
        logoutSuccessHandler.setLogoutSuccessUrl(URI.create("/login"));
        return logoutSuccessHandler;
    }
}
