package ee.taltech.crossovergame.server.astar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapConverter {
    public static final int GRID_MULTIPLIER = 1;
    private static final int TILE_SIZE = 32 / GRID_MULTIPLIER;
    private static final int TILE_AMOUNT_X = 100 * GRID_MULTIPLIER;
    private static final int TILE_AMOUNT_Y = 100 * GRID_MULTIPLIER;


    /**
     * Reads the map file and converts it to a grid
     * @return The grid of the map
     */
    public static int[][] readFile() {
        List<String> readLines = readTextFromFile();

        int[][] grid = new int[TILE_AMOUNT_Y][TILE_AMOUNT_X];

        for (int i = 0; i < TILE_AMOUNT_X; i++) {
            for (int j = 0; j < TILE_AMOUNT_Y; j++) {
                grid[i][j] = 0;
            }
        }

        // edges
        // top and bottom
        for (int i = 0; i < TILE_AMOUNT_X; i++) {
            grid[0][i] = 1;
            grid[TILE_AMOUNT_Y - 1][i] = 1;
        }

        // left and right
        for (int i = 0; i < TILE_AMOUNT_Y; i++) {
            grid[i][0] = 1;
            grid[i][TILE_AMOUNT_X - 1] = 1;
        }



        for (String line : readLines) {
            if (line.contains("<object") && !line.contains("group") && line.contains("width")) {

                // regex to match x="num" y="num" width="num" height="num"
                Pattern pattern = Pattern.compile("x=\"([0-9.\\-]+)\" y=\"([0-9.\\-]+)\" width=\"([0-9.\\-]+)\" height=\"([0-9.\\-]+)\"");
                Matcher matcher = pattern.matcher(line);

                if (line.contains("id=\"207")) {
                    System.out.println(line);
                }

                if (matcher.find()) {
                    int x = (int) (Float.parseFloat(matcher.group(1)));
                    int y = (int) (Float.parseFloat(matcher.group(2)));
                    int width = (int) (Float.parseFloat(matcher.group(3)));
                    int height = (int) (Float.parseFloat(matcher.group(4)));

                    int index_horizontal = Math.clamp(y / TILE_SIZE, 0, TILE_AMOUNT_X - 1);
                    int index_vertical = Math.clamp(x / TILE_SIZE, 0, TILE_AMOUNT_Y - 1);

                    for (int i = index_horizontal; i < index_horizontal + height / TILE_SIZE; i++) {
                        for (int j = index_vertical; j < index_vertical + width / TILE_SIZE; j++) {
                            grid[i][j] = 1;
                        }
                    }
                }
            }
        }

        return grid;
    }

    /**
     * Reads the text from the file
     * @return The list of lines from the file
     */
    private static List<String> readTextFromFile() {
        // reading file
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("map.tmx"))))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return lines;
    }

    /**
     * Main method for convertor test.
     * @param args The arguments
     */
    public static void main(String[] args) {
        int[][] grid = readFile();
        for (int[] row : grid) {
            for (int cell : row) {
                if (cell == 1) {
                    System.out.print("X");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

}
