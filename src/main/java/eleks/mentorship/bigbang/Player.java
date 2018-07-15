package eleks.mentorship.bigbang;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Created by Emiliia Nesterovych on 7/8/2018.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private UUID id;
    private String nickname;

    public Player(String nickname) {
        this.nickname = nickname;
        id = UUID.randomUUID();
    }
}
