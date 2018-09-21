package eleks.mentorship.bigbang.gameplay;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class PlayerInfo {
    private final UUID userId;
    private final String nickname;

    @JsonCreator
    public PlayerInfo(@JsonProperty("userId") UUID userId,
                      @JsonProperty("nickname") String nickname) {
        this.userId = userId;
        this.nickname = nickname;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

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
