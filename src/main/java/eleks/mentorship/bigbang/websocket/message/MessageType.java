package eleks.mentorship.bigbang.websocket.message;

public enum MessageType {
    PLAYER_CONNECTED("connect"),
    PLAYER_READY("ready"),

    START_GAME_COUNTER("start_counter"),
    GAME_START("start"),
    GAME_OVER("game_over"),

    PLAYER_MOVE("move"),
    PLAYER_PLACE_BOMB("bomb"),

    BOMB_EXPLOSION("explosion"),
    GAME_STATE("state"),
    ROOM_STATE("room")
    ;

    public static final String CONNECT_ALIAS = "connect";
    public static final String READY_ALIAS = "ready";
    public static final String START_COUNTER_ALIAS = "start_counter";
    public static final String START_ALIAS = "start";
    public static final String GAME_OVER_ALIAS = "game_over";
    public static final String MOVE_ALIAS = "move";
    public static final String BOMB_ALIAS = "bomb";
    public static final String EXPLOSION_ALIAS = "explosion";
    public static final String STATE_ALIAS = "state";
    public static final String ROOM_ALIAS = "room";

    private final String jsonAlias;

    MessageType(String jsonAlias) {
        this.jsonAlias = jsonAlias;
    }

    public String getJsonAlias() {
        return jsonAlias;
    }
}
