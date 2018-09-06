package eleks.mentorship.bigbang;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
//@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private UUID id;
    private String nickname;

    public Player(String nickname) {
        this.nickname = nickname;
        id = UUID.randomUUID();
    }
}
