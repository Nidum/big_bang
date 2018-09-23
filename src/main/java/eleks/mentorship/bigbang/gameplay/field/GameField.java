package eleks.mentorship.bigbang.gameplay.field;

import eleks.mentorship.bigbang.domain.Position;
import lombok.Data;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static eleks.mentorship.bigbang.gameplay.field.GameFieldCell.DESTROYABLE_BLOCK;
import static eleks.mentorship.bigbang.gameplay.field.GameFieldCell.FIELD;

@Data
public class GameField {
    private final List<List<GameFieldCell>> gameField;
    private final List<List<Boolean>> bombs;
    private final List<Position> spawns;

    /**
     * Reads gamefield description from some file.
     *
     * @param fileName Name of the file to be used for game field generation.
     */
    public GameField(String fileName) {
        File file = null;
        try {
            file = new ClassPathResource(fileName).getFile();
        } catch (IOException e) {
            throw new IllegalArgumentException("File " + fileName + " does not exist. Game field can not be generated");
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            this.gameField = new ArrayList<>();
            this.bombs = new ArrayList<>();
            this.spawns = new ArrayList<>();

            while (line != null) {
                List<GameFieldCell> row = line.chars()
                        .mapToObj(c -> (char) c)
                        .map(GameFieldCell::getByChar)
                        .collect(Collectors.toList());
                int size = row.size();
                bombs.add(IntStream.range(0, size).mapToObj(x -> false).collect(Collectors.toList()));

                line = reader.readLine();
                gameField.add(row);
            }

            registerSpawns();
            //TODO: Validate field sizes (each width same for all rows).

        } catch (Exception e) {
            throw new IllegalArgumentException("File " + file.getPath() + " can not be read. Game field can not be generated");
        }
    }

    public void destroyBlocksOnExplosion(ExplosionRange explosionRange) {
        Position explosionCenter = explosionRange.getCenter();
        int leftX = explosionCenter.getX() - explosionRange.getLeft();
        int rightX = explosionCenter.getX() + explosionRange.getRight();

        for (int i = leftX; i <= rightX; i++) {
            if (gameField.get(explosionCenter.getY()).get(i).equals(DESTROYABLE_BLOCK)) {
                gameField.get(explosionCenter.getY()).set(i, FIELD);
            }
        }

        int upY = explosionCenter.getY() - explosionRange.getUp();
        int downY = explosionCenter.getY() + explosionRange.getDown();
        for (int i = upY; i <= downY; i++) {
            if (gameField.get(i).get(explosionCenter.getX()).equals(DESTROYABLE_BLOCK)) {
                gameField.get(i).set(explosionCenter.getX(), FIELD);
            }
        }
    }

    public int getWidth() {
        return gameField.get(0).size();
    }

    public int getHeight() {
        return gameField.size();
    }

    public List<Position> getSpawns() {
        return spawns;
    }

    public boolean isCellAvailableForMove(Position position) {
        return gameField.get(position.getY()).get(position.getX()).equals(GameFieldCell.SPAWN) ||
                gameField.get(position.getY()).get(position.getX()).equals(GameFieldCell.FIELD);
    }

    private void registerSpawns() {
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                if (gameField.get(i).get(j).equals(GameFieldCell.SPAWN)) {
                    spawns.add(new Position(j, i));
                }
            }
        }
    }
}
