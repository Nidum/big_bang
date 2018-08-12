package eleks.mentorship.bigbang.gameplay;

import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameField {
    private List<List<GameFieldCell>> gameField;

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
            throw new IllegalArgumentException("File " + file.getPath() + " does not exist. Game field can not be generated");
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            gameField = new ArrayList<>();
            while (line != null) {
                List<GameFieldCell> row = line.chars()
                        .mapToObj(c -> (char) c)
                        .map(GameFieldCell::getByChar)
                        .collect(Collectors.toList());
                line = reader.readLine();
                gameField.add(row);
            }

            //TODO: Validate field sizes (each width same for all rows).

        } catch (Exception e) {
            throw new IllegalArgumentException("File " + file.getPath() + " can not be read. Game field can not be generated");
        }
    }

    public int getWidth(){
        return gameField.get(0).size();
    }

    public int getHeight(){
        return gameField.size();
    }

    public List<List<GameFieldCell>> getGameField() {
        return gameField;
    }
}