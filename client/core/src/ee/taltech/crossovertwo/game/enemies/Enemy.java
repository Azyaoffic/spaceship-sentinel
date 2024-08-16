package ee.taltech.crossovertwo.game.enemies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import ee.taltech.crossovertwo.Score;
import ee.taltech.crossovertwo.game.GameScreen;
import ee.taltech.crossovertwo.game.bullet.Bullet;
import ee.taltech.crossovertwo.game.items.weapon.BulletType;
import ee.taltech.crossovertwo.game.players.Player;
import ee.taltech.crossovertwo.packets.PacketEnemies;
import ee.taltech.crossovertwo.utilities.GraphicVariables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Enemy extends Sprite {
    private static int globalEnemyTimer = 0;

    public int netId;
    public float x;
    public float y;
    public float rotation;
    public int width;
    public int height;
    public Texture texture;
    public int hp;
    public int targetX;
    public int targetY;
    public static List<Map<String, String>> receivedEnemyList;
    public static List<Enemy> enemies = new ArrayList<>();
    private static BitmapFont font = GraphicVariables.getGeneralFont();
    private static Map<String, Texture> stringTextureMap = new HashMap<>();
    private static Texture defaultTexture = new Texture("enemy2.png");
    private static SpriteBatch batch = new SpriteBatch();
    private int damage;

    /**
     * Constructor for the Enemy
     * @param x The x coordinate of the enemy
     * @param y The y coordinate of the enemy
     * @param width The width of the enemy
     * @param height The height of the enemy
     * @param texture The texture of the enemy
     * @param hp The health points of the enemy
     */
    public Enemy(int x, int y, float rotation, int width, int height, String texture, int hp, int targetX, int targetY, int damage) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.width = width;
        this.height = height;
        this.texture = stringTextureMap.getOrDefault(texture, defaultTexture);
        this.hp = hp;
        this.targetX = targetX;
        this.targetY = targetY;
        this.damage = damage;
    }

    /**
     * Increment the global timer for the enemies
     */
    public static void incrementGlobalTimer() {
        globalEnemyTimer++;
    }

    /**
     * Create textures for the enemies
     */
    public static void createTextures() {
        stringTextureMap.put("enemy2.png", new Texture("enemy2.png"));
        stringTextureMap.put("enemy3.png", new Texture("enemy3.png"));
        stringTextureMap.put("enemy1.png", new Texture("enemy1.png"));
    }

    /**
     * Update the fonts of the enemies
     */
    public static void updateFonts() {
        Enemy.font = GraphicVariables.getGeneralFont();
    }

    /**
     * Register shoot.
     * @return bullet that was added.
     */
    public Bullet shootBullet() {
        if ((Math.abs(this.x - this.targetX) < 400 && Math.abs(this.y - this.targetY) < 400) && Enemy.globalEnemyTimer % 60 == 0) {
            return new Bullet((int) this.x,(int) this.y, this.targetX, this.targetY, -10, 60, damage, BulletType.BULLETRED);
        } else {
            return null;
        }
    }

    /**
     * Create an amount of enemies.
     * @param amount_of_enemies amount to add
     * @return list of created enemy objects
     */
    public static void spawnEnemyWave(int amount_of_enemies) {
        PacketEnemies.createWave(GameScreen.client, amount_of_enemies);
    }

    /**
     * Convert enemy id to enemy object.
     * @param enemyId id of enemy
     * @return enemy object
     */
    public static Enemy convertEnemyIdToEnemy(int enemyId) {
        for (Enemy enemy : enemies) {
            if (enemy.netId == enemyId) {
                return enemy;
            }
        }
        return null;
    }

    /**
     * Convert enemy map list to enemy objects.
     * @param object list of enemy maps
     */
    public static void convertEnemyMapList(List<Map<String, String>> object) {
        List<Enemy> tempEnemies = new ArrayList<>();
        for (Map<String, String> enemyMap : object) {
            int x = Integer.parseInt(enemyMap.get("x"));
            int y = Integer.parseInt(enemyMap.get("y"));
            float rotation = Float.parseFloat(enemyMap.get("rotation"));
            int width = Integer.parseInt(enemyMap.get("width"));
            int height = Integer.parseInt(enemyMap.get("height"));
            String texture = enemyMap.get("texture");
            int hp = Integer.parseInt(enemyMap.get("hp"));
            int netId = Integer.parseInt(enemyMap.get("netId"));
            int targetX = Integer.parseInt(enemyMap.get("targetX"));
            int targetY = Integer.parseInt(enemyMap.get("targetY"));
            int damage = Integer.parseInt(enemyMap.get("damage"));
            Enemy enemy = new Enemy(x, y, rotation, width, height, texture, hp, targetX, targetY, damage);
            enemy.netId = netId;
            tempEnemies.add(enemy);
        }
        enemies = new ArrayList<>(tempEnemies);
    }

    /**
     * Render all enemies and calculate their damage and collision.
     * @param camera of game screen
     * @param player unused
     */
    public static void render(OrthographicCamera camera, Player player) {
        // Ask server for enemies
        PacketEnemies.askForEnemies(GameScreen.client);
        if (receivedEnemyList != null) {
            convertEnemyMapList(receivedEnemyList);
        }

        // Calculate everything regarding enemies
        List<Enemy> enemiesToRender = new ArrayList<>(enemies);
        for (Enemy enemy : enemiesToRender) {

            // rendering alive enemy
            batch.begin();
            batch.setProjectionMatrix(camera.combined);
            batch.draw(enemy.texture, enemy.x - 32, enemy.y - 40, 32, 32, 64, 64, 1, 1, enemy.rotation, 0, 0, 64, 64, false, false);
            GraphicVariables.layout.setText(GraphicVariables.getGeneralFont(), enemy.hp + "hp");
            float textWidth = GraphicVariables.layout.width;
            GraphicVariables.getGeneralFont().draw(batch, GraphicVariables.layout, (int) enemy.x - textWidth / 2, (int) enemy.y + enemy.texture.getHeight() - 10);
            batch.end();

            // damaging enemy
            Rectangle enemyRectangle = new Rectangle(enemy.x - 32, enemy.y - 40, enemy.width, enemy.height);
            for (Bullet bullet : Bullet.getBullets()) {

                if (enemyRectangle.contains(bullet.getX(), bullet.getY()) && bullet.getTTL() > 0 && bullet.getOwner() != -10) {
//                    System.out.println("enemy hit");
                    if (enemy.hp <= bullet.getDamage() && bullet.getOwner() == 1) {
                        Player.inventory.addMoney(40);
                        Score.getInstance().addKill();
                    }
                    Score.getInstance().addHit();
                    PacketEnemies.damageEnemy(GameScreen.client, enemy.netId, bullet.getDamage());
                    bullet.deleteBullet();
                }
            }

            Bullet enemyBullet = enemy.shootBullet();
//            System.out.println("enemy bullet: " + enemyBullet);
            if (enemyBullet != null) {
                Bullet.addBullet(enemyBullet);
            }

        }
        enemiesToRender.clear();
        enemiesToRender = null;
    }

    /**
     * Add enemies to the list of received enemies when received.
     * @param enemies that received.
     */
    public static void addReceivedEnemiesList(List<Map<String, String>> enemies) {
        receivedEnemyList = enemies;
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

    /**
     * Dispose all enemies and delete them from hte list.
     */
    public static void dispose() {
        for (Enemy enemy : enemies) {
            enemy.texture.dispose();
        }
        enemies.clear();
    }
}
