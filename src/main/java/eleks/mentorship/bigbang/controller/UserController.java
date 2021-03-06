package eleks.mentorship.bigbang.controller;

import eleks.mentorship.bigbang.gameplay.PlayerInfo;
import eleks.mentorship.bigbang.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
public class UserController {

    @GetMapping("/api/user")
    public Mono<PlayerInfo> getUser(Mono<Principal> principal) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .map(p -> (UserPrincipal) p)
                .map(p->new PlayerInfo(p.getId(), p.getNickname()));
    }

}
