package ee.taltech.crossovergame.server.enemies;

import ee.taltech.crossovergame.server.GameServer;
import ee.taltech.crossovergame.server.astar.AStar;
import ee.taltech.crossovergame.server.utilities.Vector2D;

import java.util.List;
import java.util.Map;


public class Enemy {

    private static final int MOTHERSHIP_X = 1504 + 192 / 2;  // mothership centre roughly?
    private static final int MOTHERSHIP_Y = 1472 + 224 / 2;

    static int globalEnemyTimer = 0;
    private static void incrementTimer() {
        globalEnemyTimer++;
    }

    private static int currentId = 0;
    private static void incrementNextId() {
        currentId++;
    }
    public int netId;
    public int x;
    public int y;
    public float rotation;
    public int width;
    public int height;
    public String textureFile;
    public int hp;
    public boolean isDead = false;
    private int speed = 2;
    public int targetX;
    public int targetY;
    public int targetPlayerX;
    public int targetPlayerY;
    private List<AStar.Node> path;
    public int targetPlayerClientId = -10;
    private int lastGoodPlayerId;
    GameServer gameServer;
    public int damage = 25;

    /**
     * Constructor for the Enemy
     * @param x The x coordinate of the enemy
     * @param y The y coordinate of the enemy
     * @param width The width of the enemy
     * @param height The height of the enemy
     * @param texture The texture of the enemy
     * @param hp The health points of the enemy
     */
    public Enemy(int x, int y, int width, int height, String texture, int hp, GameServer gameServer, int damage, int speed) {
        this.x = x;
        this.y = y;
        this.targetX = this.x;
        this.targetY = this.y;
        this.targetPlayerX = MOTHERSHIP_X;
        this.targetPlayerY = MOTHERSHIP_Y;
        this.rotation = 0;
        this.width = width;
        this.height = height;
        this.textureFile = texture;
        this.hp = hp;
        this.netId = currentId;
        incrementNextId();
        this.gameServer = gameServer;
        this.damage = damage;
        this.speed = speed;
    }

    /**
     * Calculate the target cell for the enemy
     * @param targetX The x coordinate of the target
     * @param targetY The y coordinate of the target
     */
    private void calculateTargetCell(int targetX, int targetY) {
        int targetPlayerXCell = targetX / 32;
        int targetPlayerYCell = targetY / 32;

        this.path = gameServer.aStar.findPath
                (this.x / 32, this.y / 32, targetPlayerXCell, targetPlayerYCell);
    }

    /**
     * Calculate the rotation of the enemy
     * @param targetX The x coordinate of the target
     * @param targetY The y coordinate of the target
     */
    private void calculateRotation(int targetX, int targetY) {
        this.rotation = (float) (Math.toDegrees(Math.atan2(targetX - this.x, targetY - this.y)) + 180) % 360;
    }


    /**
     * Move the enemy towards the target coordinates
     */
    public void moveEnemy() {
        incrementTimer();

        if (this.targetPlayerClientId == -10) { // -10 means wandering
            wander();
            for (Map.Entry<Integer, String> clientPos : gameServer.clientPositions.entrySet()) {

                if (gameServer.getStatuses().get(clientPos.getKey()).get("status").equals("false")) {
                    continue;
                }

                if (!gameServer.botPlayers.isEmpty() && Math.abs(this.x - gameServer.botPlayers.getFirst().x) < 200
                        && Math.abs(this.y - gameServer.botPlayers.getFirst().y) < 200) {

                    this.targetPlayerClientId = -1; // -1 means bot

                    // quick fix for A* calculation
                    this.targetX = this.x;
                    this.targetY = this.y;
                    break;
                }

                int xCoord = Integer.parseInt(clientPos.getValue().split(";;")[0]);
                int yCoord = 3200 - Integer.parseInt(clientPos.getValue().split(";;")[1]);

                if (Math.abs(this.x - xCoord) < 200 && Math.abs(this.y - yCoord) < 200) {
                    // since targetXY are always sent and are always a player's coordinate, we can check if it's close
                    // to any player which can see the enemy
                    this.targetPlayerClientId = clientPos.getKey();
                    this.lastGoodPlayerId = this.targetPlayerClientId;

                    // quick fix for A* calculation
                    this.targetX = this.x;
                    this.targetY = this.y;
                    break;
                }
            }
        } else {
            if (this.targetPlayerClientId != -1) {
                try {
                    this.targetPlayerX = Integer.parseInt(gameServer.clientPositions.get(this.targetPlayerClientId).split(";;")[0]) + 16;
                    this.targetPlayerY = 3200 - Integer.parseInt(gameServer.clientPositions.get(this.targetPlayerClientId).split(";;")[1]) + 10;
                } catch (NullPointerException e) {
                    System.out.println("Client not found: " + this.targetPlayerClientId);
                }

            } else {
                try {
                    this.targetPlayerX = gameServer.botPlayers.getFirst().x;
                    this.targetPlayerY = gameServer.botPlayers.getFirst().y;
                } catch (Exception e) {
                    this.targetPlayerClientId = this.lastGoodPlayerId;

                    this.targetPlayerX = MOTHERSHIP_X;
                    this.targetPlayerY = MOTHERSHIP_Y;
                }
            }


            // if too far or dead - forget the player, go for wandering
            if (this.targetPlayerClientId != -1 && this.targetPlayerClientId != 0 &&
                    gameServer.getStatuses().get(targetPlayerClientId).get("status").equals("false")) {
                this.targetPlayerClientId = -10;
                wander();
                return;
            }

            if (Math.abs(this.x - this.targetPlayerX) > 500 || Math.abs(this.y - this.targetPlayerY) > 500) {
                this.targetPlayerClientId = -10;
                wander();
                return;
            }

            // we should calculate this only when the enemy is close to the target point
            if (Math.abs(this.targetX - this.x) <= 3 && Math.abs(this.targetY - this.y) <= 3) {
                System.out.println(String.format("pX:%s;pY:%s;pXCell:%s;pYCell:%s;isInColl:%s", this.targetPlayerX, this.targetPlayerY, this.targetPlayerX / 32, this.targetPlayerY / 32, gameServer.mapGrid[this.targetPlayerY / 32][this.targetPlayerX / 32] == 1));

                calculateTargetCell(this.targetPlayerX, this.targetPlayerY);
                calculateRotation(this.targetPlayerX, this.targetPlayerY);
                try {
                    if (this.path.size() > 1) {
                        this.targetX = this.path.get(this.path.size() - 2).x * 32;
                        this.targetY = this.path.get(this.path.size() - 2).y * 32;
                    } else {
                        this.targetX = this.path.get(0).x * 32;
                        this.targetY = this.path.get(0).y * 32;
                    }
                } catch (Exception e) {
                    System.out.println(this);
                    this.targetPlayerClientId = -10;
                    wander();
                    return;
                }
            }

            Vector2D movementVector = new Vector2D(this.targetX, this.targetY);
            movementVector.subtract(this.x, this.y);
            movementVector.normalize();

            // ^ vector from enemy to player, normalized
            System.out.println("movementVector: " + Math.round(movementVector.x) + " | " + Math.round(movementVector.y));

            float moveX = speed * Math.round(movementVector.x);
            float moveY = speed * Math.round(movementVector.y);

            int playerSize = gameServer.getClients().size();

            if ((moveX / playerSize < 1 && moveX / playerSize > 0) || (moveY / playerSize < 1 && moveY / playerSize > 0) ||
                    (moveX / playerSize > -1 && moveX / playerSize < 0) || (moveY / playerSize > -1 && moveY / playerSize < 0)) {
                this.x += (int) moveX;
                this.y += (int) moveY;
            } else {
                this.x += (int) ((speed * Math.round(movementVector.x)) / gameServer.getClients().size());
                this.y += (int) ((speed * Math.round(movementVector.y)) / gameServer.getClients().size());
            }
        }
    }

    /**
     * Wander around the map.
     */
    public void wander() {

        calculateTargetCell(MOTHERSHIP_X, MOTHERSHIP_Y);
        calculateRotation(MOTHERSHIP_X, MOTHERSHIP_Y);

        try {
            if (this.path.size() > 1) {
                this.targetX = this.path.get(this.path.size() - 2).x * 32 + 16;
                this.targetY = this.path.get(this.path.size() - 2).y * 32 + 16;
            } else {
                this.targetX = this.path.get(0).x * 32 + 16;
                this.targetY = this.path.get(0).y * 32 + 16;
            }
        } catch (Exception e) {
            System.out.println(this);
            throw new RuntimeException(x + " | " + y + e);
        }

        Vector2D movementVector = new Vector2D(this.targetX, this.targetY);
        movementVector.subtract(this.x, this.y);
        movementVector.normalize();

        // Fixing aiming and target coordinates
        this.targetPlayerX = MOTHERSHIP_X;
        this.targetPlayerY = MOTHERSHIP_Y;

        float moveX = speed * Math.round(movementVector.x);
        float moveY = speed * Math.round(movementVector.y);

        int playerSize = gameServer.getClients().size();

        if ((moveX / playerSize < 1 && moveX / playerSize > 0) || (moveY / playerSize < 1 && moveY / playerSize > 0) ||
            (moveX / playerSize > -1 && moveX / playerSize < 0) || (moveY / playerSize > -1 && moveY / playerSize < 0)) {
            this.x += (int) moveX;
            this.y += (int) moveY;
        } else {
            this.x += (int) ((speed * Math.round(movementVector.x)) / gameServer.getClients().size());
            this.y += (int) ((speed * Math.round(movementVector.y)) / gameServer.getClients().size());
        }
    }

    /**
     * Damage enemy by dmg.
     * @param dmg
     */
    public void damageEnemy(int dmg) {
        this.hp -= dmg;
        if (this.hp <= 0) {
            gameServer.enemies.remove(this);
        }
    }

    // enemy is the same if its netid is the same
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Enemy enemy = (Enemy) o;

        return netId == enemy.netId;
    }

    @Override
    public int hashCode() {
        return netId;
    }

    @Override
    public String toString() {
        return "Enemy{" + "\n" +
                "netId=" + netId + "\n" +
                ", x=" + x + "\n" +
                ", y=" + y + "\n" +
                ", rotation=" + rotation + "\n" +
                ", width=" + width + "\n" +
                ", height=" + height + "\n" +
                ", textureFile='" + textureFile + '\'' + "\n" +
                ", hp=" + hp + "\n" +
                ", isDead=" + isDead + "\n" +
                ", speed=" + speed + "\n" +
                ", targetX=" + targetX + "\n" +
                ", targetY=" + targetY + "\n" +
                ", targetPlayerX=" + targetPlayerX + "\n" +
                ", targetPlayerY=" + targetPlayerY + "\n" +
                ", path=" + path + "\n" +
                ", targetPlayerClientId=" + targetPlayerClientId + "\n" +
                ", lastGoodPlayerId=" + lastGoodPlayerId + "\n" +
                ", gameServer=" + gameServer + "\n" +
                ", damage=" + damage + "\n" +
                '}';
    }
}
