package eleks.mentorship.bigbang.gameplay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerInfo {
    private UUID userId;
    private String nickname;
    // TODO: add avatar.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerInfo that = (PlayerInfo) o;

        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        return nickname != null ? nickname.equals(that.nickname) : that.nickname == null;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (nickname != null ? nickname.hashCode() : 0);
        return result;
    }
}
