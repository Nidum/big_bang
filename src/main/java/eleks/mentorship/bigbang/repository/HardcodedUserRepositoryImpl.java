//package eleks.mentorship.bigbang.repository;
//
//import eleks.mentorship.bigbang.dbo.User;
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;
//
////@Repository
//@Component
//@AllArgsConstructor
//public class HardcodedUserRepositoryImpl implements UserRepository {
//
////    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public Mono<User> findUser(String email) {
//        String boomerMail = "boomer@gmail.com";
//        String boomerPass = "boom";
//        String momMail = "mom@gmail.com";
//        String momPass = "mom";
////        if (email.equals(boomerMail)) {
////            return Mono.just(
////                    new User(UUID.fromString(""), "BOOMer", passwordEncoder.encode(boomerPass), boomerMail));
////        } else if (email.equals(momMail)) {
////            return Mono.just(
////                    new User(UUID.fromString(""), "=MoM=", passwordEncoder.encode(momPass), momMail));
////        }
//        return Mono.empty();
//    }
//}
