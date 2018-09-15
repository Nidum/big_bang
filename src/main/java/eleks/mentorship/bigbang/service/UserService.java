package eleks.mentorship.bigbang.service;

import eleks.mentorship.bigbang.repository.UserRepository;
import eleks.mentorship.bigbang.security.UserPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class UserService implements ReactiveUserDetailsService {

    private final UserRepository repository;

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return repository.findUser(email).map(UserPrincipal::new);
    }

}