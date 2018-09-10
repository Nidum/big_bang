package eleks.mentorship.bigbang.repository;

import eleks.mentorship.bigbang.dbo.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> findUser(String email);
}
