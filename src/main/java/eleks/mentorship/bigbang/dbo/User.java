package eleks.mentorship.bigbang.dbo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class User {
    private final UUID id;
    private String nickname;
    private final String password;
    private final String email;
}
