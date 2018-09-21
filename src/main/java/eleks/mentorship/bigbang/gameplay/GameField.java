package eleks.mentorship.bigbang.gameplay;

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

import static eleks.mentorship.bigbang.gameplay.GameFieldCell.SPAWN;

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
            gameField = new ArrayList<>();
            bombs = new ArrayList<>();
            spawns = new ArrayList<>();

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

    public int getWidth() {
        return gameField.get(0).size();
    }

    public int getHeight() {
        return gameField.size();
    }

    public List<Position> getSpawns() {
        return spawns;
    }

    private void registerSpawns() {
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                if (gameField.get(i).get(j).equals(SPAWN)) {
                    spawns.add(new Position(j, i));
                }
            }
        }
    }
}
