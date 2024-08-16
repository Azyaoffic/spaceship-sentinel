package ee.taltech.crossovergame.server.astar;
// A* test

import java.util.List;

public class Main {

    /**
     * Main method for the A* test
     * @param args The arguments
     */
    public static void main(String[] args) {
        int[][] grid = MapConverter.readFile();
        AStar aStar = new AStar(grid);
        List<AStar.Node> path = aStar.findPath(1,1,98,98);
        for (AStar.Node node : path) {
            System.out.printf("%s:%s%n", node.x, node.y);
        }
    }
}
