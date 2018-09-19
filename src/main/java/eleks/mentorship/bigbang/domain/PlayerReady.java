package eleks.mentorship.bigbang.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PlayerReady {
    private UUID userId;
    private String nickname;
    private Boolean ready;
}
