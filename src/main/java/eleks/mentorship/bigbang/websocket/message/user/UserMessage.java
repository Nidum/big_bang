package eleks.mentorship.bigbang.websocket.message.user;

import eleks.mentorship.bigbang.gameplay.PlayerInfo;
import eleks.mentorship.bigbang.websocket.message.GameMessage;


public abstract class UserMessage extends GameMessage {
//    @JsonIgnore
    protected PlayerInfo playerInfo;

//    @JsonProperty
    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

//    @JsonIgnore
    public void setPlayerInfo(PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }
}
