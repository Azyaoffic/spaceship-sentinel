package ee.taltech.crossovergame.server.bots;

import ee.taltech.crossovergame.server.GameServer;
import ee.taltech.crossovergame.server.astar.AStar;
import ee.taltech.crossovergame.server.enemies.Enemy;
import ee.taltech.crossovergame.server.utilities.Vector2D;

import java.util.List;
import java.util.Random;

public class BotPlayer {
    private static int globalId = 0;
    public int id;

    public int health;
    public int damage;
    public int speed;
    public int x;
    public int y;
    public List<AStar.Node> path;
    public int targetEnemyId;
    public Enemy targetEnemy;
    private final GameServer gameServer;
    private final Random random = new Random();

    /**
     * Increment the global id
     */
    private static void incrementId() {
        globalId++;
    }

    /**
     * Constructor for the BotPlayer
     * @param gameServer The game server
     * @param health The health of the bot
     * @param damage The damage of the bot
     * @param speed The speed of the bot
     * @param x The x coordinate of the bot
     * @param y The y coordinate of the bot
     */
    public BotPlayer(GameServer gameServer, int health, int damage, int speed, int x, int y) {
        this.gameServer = gameServer;
        this.health = health;
        this.damage = damage;
        this.speed = speed;
        this.x = x;
        this.y = y;
        this.targetEnemyId = !gameServer.enemies.isEmpty() ? gameServer.enemies.get(random.nextInt(gameServer.enemies.size())).netId : -1;
        if (targetEnemyId != -1) {
            this.targetEnemy = gameServer.enemies.stream().filter(enemy -> enemy.netId == targetEnemyId).findFirst().get();
        } else {
            this.targetEnemy = null;
        }
        this.id = globalId;
        incrementId();
    }

    /**
     * Kinda the main loop
     */
    public void move() {
        checkIfEnemyStillAlive();
        if (targetEnemyId == -1) {
            return;
        }

        calculateTargetCell(targetEnemy.x, targetEnemy.y);

        if (this.path == null) {
            return;
        }

        int targetX;
        int targetY;

        if (this.path.size() > 1) {
            targetX = this.path.get(this.path.size() - 2).x * 32 + 16;
            targetY = this.path.get(this.path.size() - 2).y * 32 + 16;
        } else {
            targetX = this.path.get(0).x * 32 + 16;
            targetY = this.path.get(0).y * 32 + 16;
        }


        Vector2D movementVector = new Vector2D(targetX, targetY);
        movementVector.subtract(this.x, this.y);
        movementVector.normalize();
        // ^ vector from enemy to player, normalized
        this.x += (int) ((speed * movementVector.x) / gameServer.getClients().size());
        this.y += (int) ((speed * movementVector.y) / gameServer.getClients().size());
    }

    /**
     * Calculate the target cell for the bot
     * @param targetX The x coordinate of the target
     * @param targetY The y coordinate of the target
     */
    private void calculateTargetCell(int targetX, int targetY) {
        int targetXCell = targetX / 32;
        int targetYCell = targetY / 32;

        this.path = gameServer.aStar.findPath(x / 32, y / 32, targetXCell, targetYCell);
    }

    /**
     * Check if the enemy is still alive
     */
    private void checkIfEnemyStillAlive() {
        try {
            if (gameServer.enemies.stream().noneMatch(enemy -> enemy.netId == targetEnemyId)) {
                if (!gameServer.enemies.isEmpty()) {
                    targetEnemyId = gameServer.enemies.get(random.nextInt(gameServer.enemies.size())).netId;
                    targetEnemy = gameServer.enemies.stream().filter(enemy -> enemy.netId == targetEnemyId).findFirst().get();
                } else {
                    targetEnemyId = -1;
                }
            }
        } catch (Exception e) {
            targetEnemyId = -1;
        }
    }
}
