package eleks.mentorship.bigbang.gameplay;

/**
 * Created by Emiliia Nesterovych on 7/1/2018.
 */
public enum GameFieldCell {
    FIELD('f'),
    DESTROYABLE_BLOCK('d'),
    STATIC_BLOCK('b'),
    SPAWN('s'),

    ;

    private char fileChar;

    GameFieldCell(char fileChar) {
        this.fileChar = fileChar;
    }

    public static GameFieldCell getByChar(char c) {
        for (GameFieldCell gameFieldCell : values()) {
            if(gameFieldCell.fileChar == c){
                return gameFieldCell;
            }
        }
        throw new RuntimeException("Char is not mapped to any kind of game field cell.");
    }
}
