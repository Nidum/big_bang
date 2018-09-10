package eleks.mentorship.bigbang.repository;

import eleks.mentorship.bigbang.dbo.User;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@AllArgsConstructor
public class HardcodedUserRepositoryImpl implements UserRepository {

    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<User> findUser(String email) {
        String boomerMail = "boomer@gmail.com";
        String boomerPass = "boom";
        String momMail = "mom@gmail.com";
        String momPass = "mom";
        if (email.equals(boomerMail)) {
            return Mono.just(
                    new User(UUID.fromString("a7418e40-b512-11e8-96f8-529269fb1459"), "BOOMer", passwordEncoder.encode(boomerPass), boomerMail));
        } else if (email.equals(momMail)) {
            return Mono.just(
                    new User(UUID.fromString("a7419566-b512-11e8-96f8-529269fb1459"), "=MoM=", passwordEncoder.encode(momPass), momMail));
        }
        return Mono.empty();
    }
}
