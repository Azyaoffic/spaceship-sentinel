package ee.taltech.crossovergame.server.astar;
// https://gamedevdoc.pages.taltech.ee/pathfinding/pathfinding.html

import java.util.*;

public class AStar {
    private final int maxX;
    private final int maxY;
    private final int[][] grid;
    private final int[][] neighbours = {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};

    /**
     * Constructor for the AStar class
     * @param grid The grid of the map
     */
    public AStar(int[][] grid) {
        this.grid = grid;
        this.maxX = grid[0].length;
        this.maxY = grid.length;
    }

    /**
     * The Node class for the AStar algorithm
     */
    public class Node {
        public int x;
        public int y;
        int gScore;
        int hScore;
        Node parent;

        /**
         * Constructor for the Node class
         * @param x The x coordinate of the node
         * @param y The y coordinate of the node
         */
        Node(int x, int y) {
            this.x = x;
            this.y = y;
            this.gScore = 0;
            this.hScore = 0;
            this.parent = null;
        }

        /**
         * Update the hScore of the node
         * @param dstX The x coordinate of the destination
         * @param dstY The y coordinate of the destination
         */
        void updateHScore(int dstX, int dstY) {
            this.hScore = Math.abs(x - dstX) + Math.abs(y - dstY);
        }

        /**
         * Get the fScore of the node
         * @return The fScore of the node
         */
        int getFScore() {
            return this.gScore + this.hScore;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node node)) return false;
            return x == node.x && y == node.y;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(x + (y * maxY));
        }

        @Override
        public String toString() {
            return "Node{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    /**
     * Find the path from src to dst
     * @param srcX The x coordinate of the source
     * @param srcY The y coordinate of the source
     * @param dstX The x coordinate of the destination
     * @param dstY The y coordinate of the destination
     * @return The path from src to dst
     */
    public List<Node> findPath(int srcX, int srcY, int dstX, int dstY) {
        List<Node> path = new ArrayList<>();
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(AStar.Node::getFScore));
        openSet.add(new Node(srcX, srcY));
        Set<Node> closedSet = new HashSet<>();
        while (!openSet.isEmpty()) {
            AStar.Node current = openSet.poll();
            if (current.x == dstX && current.y == dstY) {
                while (current != null) {
                    path.add(current);
                    current = current.parent;
                }
                return path;
            }
            closedSet.add(current);
            for (int[] neighbour : neighbours) {
                int x = current.x + neighbour[0];
                int y = current.y + neighbour[1];
                if (x < 0 || x >= maxX || y < 0 || y >= maxY || grid[y][x] == 1) {
                    continue;
                }
                AStar.Node neighbor = new AStar.Node(x, y);
                int newGScore = current.gScore + 1;
                if (closedSet.contains(neighbor)) {
                    continue;
                }
                if (!openSet.contains(neighbor) || newGScore < neighbor.gScore) {
                    neighbor.parent = current;
                    neighbor.gScore = newGScore;
                    neighbor.updateHScore(dstX, dstY);
                    openSet.add(neighbor);
                }
            }
        }
        return null;
    }

}


