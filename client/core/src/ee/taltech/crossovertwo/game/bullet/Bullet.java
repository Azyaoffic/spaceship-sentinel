package ee.taltech.crossovertwo.game.bullet;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import ee.taltech.crossovertwo.game.GameScreen;
import ee.taltech.crossovertwo.game.items.weapon.BulletType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Bullet {

    private float x;
    private float y;
    // float rotation;
    private float dx;
    private float dy;
    private static int speed = 18;
    private float rotation;
    private int TTL;
    private int damage;
    boolean isVisible = true;
    private int owner;
    private BulletType type;

    private static Texture bullet = new Texture("bullet.png");
    private static Texture bulletPlasmBlue = new Texture("bulletPlasmBlue.png");
    private static Texture bulletPlasmRed = new Texture("bulletPlasmRed.png");
    private static List<Bullet> bullets = new ArrayList<>();
    public static Map<Integer, Map<String, String>> receivedBullets;

    private static SpriteBatch bulletBatch = new SpriteBatch();

    /**
     * Method to update the textures after each game.
     */
    public static void updateTextures() {
        bullet = new Texture("bullet.png");
        bulletPlasmBlue = new Texture("bulletPlasmBlue.png");
        bulletPlasmRed = new Texture("bulletPlasmRed.png");
    }

    /**
     * Constructor to initialize the Bullet
     * @param x The x coordinate of the bullet
     * @param y The y coordinate of the bullet
     * @param rotation The rotation of the bullet
     */
    public Bullet(int x, int y, float rotation, int owner, int TTL, int damage, BulletType type) {
        this.rotation = rotation;
        this.x = x + 20;
        this.y = y + 20;
        this.TTL = TTL;
        this.damage = damage;
        dx = (float) -(speed * Math.sin(rotation / 57.3)); // - to flip the dx
        dy = (float) (speed * Math.cos(rotation / 57.3));
        this.owner = owner;
        this.type = type;
    }

    /**
     * Constructor to initialize the Bullet
     * @param x The x coordinate of the bullet
     * @param y The y coordinate of the bullet
     * @param targetX The x coordinate of the target
     * @param targetY The y coordinate of the target
     */
    public Bullet(int x, int y, int targetX, int targetY, int owner, int TTL, int damage, BulletType type) {
        Vector2 movementVector = new Vector2(targetX, targetY).sub(x, y).nor();
        this.rotation = movementVector.angle();
        this.x = x + 20;
        this.y = y + 20;
        this.TTL = TTL;
        this.damage = damage;
        dx = (float) speed * movementVector.x;
        dy = (float) speed * movementVector.y;
        this.owner = owner;
        this.type = type;
    }

    /**
     * Method to move the bullet
     * This method moves the bullet in the direction of the dx and dy
     * It also decreases the TTL of the bullet
     */
    public void move() {
        x += dx;
        y += dy;
        TTL--;
    }

    /**
     * This method renders the bullets
     * @param camera The camera to render the bullets on
     */
    public static void render(OrthographicCamera camera) {
        saveOtherBullet();
        if (!(bullets.isEmpty())){
            for (Bullet bullet : bullets) {
                if (bullet == null) {
                    continue;
                }
                bullet.move();
                if (bullet.getTTL() < 1) {
                    bullet = null;
                    continue;
                }

                bulletBatch.begin();
                bulletBatch.setProjectionMatrix(camera.combined);
                if (bullet.type == BulletType.BULLET) {
                    bulletBatch.draw(Bullet.bullet, (int) bullet.getX(), (int) bullet.getY(), 0, 0,
                            2, 15, 1, 1, bullet.rotation, 0, 0,
                            10, 10, false, false);
                } else if (bullet.type == BulletType.BULLETBLUE) {
                    bulletBatch.draw(Bullet.bulletPlasmBlue, (int) bullet.getX(), (int) bullet.getY(), 10, 10);
                } else {
                    bulletBatch.draw(Bullet.bulletPlasmRed, (int) bullet.getX(), (int) bullet.getY(), 10, 10);
                }
                bulletBatch.end();
            }
        }
    }

    /**
     * Method to save other bullets
     */
    public static void saveOtherBullet() {
        // Saving enemy bullets
        if (!(receivedBullets == null)) {
            for (Map.Entry<Integer, Map<String, String>> entry : receivedBullets.entrySet()) {
                int id = entry.getKey();
                if (id == GameScreen.clientId || id == 0) continue;

                float receivedXCoord = Float.parseFloat(entry.getValue().get("x"));
                float receivedYCoord = Float.parseFloat(entry.getValue().get("y"));
                float receivedAngle = Float.parseFloat(entry.getValue().get("angle"));
                BulletType receivedType = BulletType.valueOf(entry.getValue().get("bulletType"));
                bullets.add(new Bullet((int) receivedXCoord, (int) receivedYCoord, receivedAngle, 0, 60, 25, receivedType));
            }
            receivedBullets = null;
        }
    }

    /**
     * Method to dispose the bullet
     */
    public static void dispose() {
        bullet.dispose();
        bulletPlasmBlue.dispose();
        bulletPlasmRed.dispose();
        bullets.clear();
    }

    public static List<Bullet> getBullets() {
        return bullets;
    }

    /**
     * Method to add a bullet
     * @param bullet The bullet to add
     */
    public static void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    /**
     * Get the x coordinate of the bullet
     * @return x coordinate
     */
    public float getX() {
        return x;
    }

    /**
     * Get the y coordinate of the bullet
     * @return y coordinate
     */
    public float getY() {
        return y;
    }

    /**
     * Get time to live of the bullet
     * @return TTL
     */
    public int getTTL() {
        return TTL;
    }

    /**
     * Get the damage of the bullet
     * @return damage
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Get the owner of the bullet
     * @return owner
     */
    public int getOwner() {
        return owner;
    }

    /**
     * Method to make bullet irrelevant
     */
    public void deleteBullet() {
        this.isVisible = true;
        this.TTL = 0;
    }

    /**
     * Set the owner of the bullet
     * @param owner The owner of the bullet
     */
    public void setOwner(int owner) {
        this.owner = owner;
    }
}
