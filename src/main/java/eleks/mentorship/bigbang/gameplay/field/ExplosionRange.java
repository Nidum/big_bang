package eleks.mentorship.bigbang.gameplay.field;

import eleks.mentorship.bigbang.domain.Position;
import lombok.Getter;

@Getter
public class ExplosionRange {
    private int up;
    private int down;
    private int left;
    private int right;
    private Position center;

    private ExplosionRange() {
    }

    public static ExplosionRange getExplosionRange(GameField gameField, Position bombPosition, int explosionRange) {
        ExplosionRange range = new ExplosionRange();
        range.center = bombPosition;
        int height = gameField.getHeight();
        int width = gameField.getWidth();

        // Left.
        for (int i = bombPosition.getX() - 1, j = 1; i >= 0 && i > bombPosition.getX() - explosionRange; i--, j++) {
            GameFieldCell nextCell = gameField.getGameField().get(bombPosition.getY()).get(i);
            range.left = j;
            if (nextCell.equals(GameFieldCell.STATIC_BLOCK)) {
                range.left = j - 1;
                break;
            }
            if (nextCell.equals(GameFieldCell.DESTROYABLE_BLOCK)) {
                break;
            }
        }

        // Right.
        for (int i = bombPosition.getX() + 1, j = 1; i < width && i < bombPosition.getX() + explosionRange; i++, j++) {
            GameFieldCell nextCell = gameField.getGameField().get(bombPosition.getY()).get(i);
            range.right = j;

            if (nextCell.equals(GameFieldCell.STATIC_BLOCK)) {
                range.right = j - 1;
                break;
            }
            if (nextCell.equals(GameFieldCell.DESTROYABLE_BLOCK)) {
                break;
            }
        }

        // Up.
        for (int i = bombPosition.getY() - 1, j = 1; i >= 0 && i > bombPosition.getY() - explosionRange; i--, j++) {
            GameFieldCell nextCell = gameField.getGameField().get(i).get(bombPosition.getX());
            range.up = j;
            if (nextCell.equals(GameFieldCell.STATIC_BLOCK)) {
                range.up = j - 1;
                break;
            }
            if (nextCell.equals(GameFieldCell.DESTROYABLE_BLOCK)) {
                break;
            }
        }

        // Down.
        for (int i = bombPosition.getY() + 1, j = 1; i < height && i < bombPosition.getY() + explosionRange; i++, j++) {
            GameFieldCell nextCell = gameField.getGameField().get(i).get(bombPosition.getX());
            range.down = j;
            if (nextCell.equals(GameFieldCell.STATIC_BLOCK)) {
                range.down = j - 1;
                break;
            }
            if (nextCell.equals(GameFieldCell.DESTROYABLE_BLOCK)) {
                break;
            }
        }

        return range;
    }

    public boolean isInRange(Position position){
        int left = center.getX() - this.left;
        int right = center.getX() + this.right;
        if(position.getY() == center.getY() &&
                position.getX() >= left &&
                position.getX() <= right){
            return true;
        }

        int up = center.getY() - this.up;
        int down = center.getY() + this.down;
        return position.getX() == center.getX() &&
                position.getY() >= up &&
                position.getY() <= down;

    }

}