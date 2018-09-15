package eleks.mentorship.bigbang.gameplay;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PlayerInfo {
    private final UUID userId;
    private final String nickname;
    // TODO: add avatar.
}
