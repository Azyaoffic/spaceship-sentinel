package ee.taltech.crossovergame.server.turrets;

import ee.taltech.crossovergame.server.GameServer;
import ee.taltech.crossovergame.server.enemies.Enemy;

import java.util.Comparator;

public class Turret {

    private static int globalId = 0;
    public int id;
    public int x;
    public int y;
    public float rotationAngle;
    public float lastRotationAngle;
    public int health;
    public int damage;
    public int range;
    public int fireRate;
    public String texture;
    GameServer gameServer;

    public int targetEnemyId;

    public Enemy targetEnemy;

    /**
     * Increment the global id
     */
    private static void incrementId() {
        globalId++;
    }

    /**
     * Constructor for the Turret
     * @param gameServer The game server
     * @param x The x coordinate of the turret
     * @param y The y coordinate of the turret
     * @param rotationAngle The rotation angle of the turret
     * @param health The health of the turret
     * @param damage The damage of the turret
     * @param range The range of the turret
     * @param fireRate The fire rate of the turret
     */
    public Turret(GameServer gameServer, int x, int y, float rotationAngle, int health, int damage, int range, int fireRate, String texture) {
        this.gameServer = gameServer;
        this.x = x;
        this.y = y;
        this.rotationAngle = rotationAngle;
        this.health = health;
        this.damage = damage;
        this.range = range;
        this.fireRate = fireRate;
        this.texture = texture;
        FindTargetEnemy();
        this.id = globalId;
        incrementId();
    }

    /**
     * Find the target enemy
     */
    public void FindTargetEnemy() {
        if (gameServer.enemies.isEmpty()) {
            targetEnemy = null;
            targetEnemyId = -1;
        } else {
            targetEnemy = gameServer.enemies.stream()
                    .min(Comparator.comparingDouble(enemy -> Math.sqrt(Math.pow(enemy.x - this.x, 2) + Math.pow(enemy.y - this.y, 2)))).get();
            targetEnemyId = targetEnemy.netId;
        }
    }

    /**
     * Rotate the turret
     */
    public void rotate() {
        if (targetEnemyId == -1) {
            rotationAngle = lastRotationAngle;
        } else {
            lastRotationAngle = rotationAngle;
            rotationAngle = (float) (Math.toDegrees(Math.atan2(targetEnemy.x - x, targetEnemy.y - y)) + 180) % 360;
        }
    }

    /**
     * Damage enemy by dmg.
     * @param dmg
     */
    public void damageTurret(int dmg) {
        this.health -= dmg;
        if (this.health <= 0) {
            gameServer.turrets.remove(this);
        }
    }
}
