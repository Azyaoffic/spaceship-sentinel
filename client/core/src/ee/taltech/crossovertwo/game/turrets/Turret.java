package ee.taltech.crossovertwo.game.turrets;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ee.taltech.crossovertwo.Score;
import ee.taltech.crossovertwo.game.GameScreen;
import ee.taltech.crossovertwo.game.bullet.Bullet;
import ee.taltech.crossovertwo.game.enemies.Enemy;
import ee.taltech.crossovertwo.game.items.weapon.BulletType;
import ee.taltech.crossovertwo.game.players.Player;
import ee.taltech.crossovertwo.packets.PacketTurrets;
import ee.taltech.crossovertwo.utilities.GraphicVariables;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Turret {
    private static int globalTurretTimer = 1;
    private static void incrementTimer() {
        globalTurretTimer++;
    }

    public String name;
    public int x;
    public int y;
    public int width;
    public int height;
    public float rotationAngle;
    public int health;
    public int damage;
    public int range;
    public int fireRate;
    public int id;
    public int targetEnemyId;
    private Enemy targetEnemy;
    private Texture texture;
    private BulletType bulletType;
    public static List<Turret> turrets = new ArrayList<>();
    public static SpriteBatch turretBatch;
    public static Texture basicTurretTexture = new Texture("basicTurret.png");
    public static Texture plasmTurretTexture = new Texture("plasmTurret.png");

    public Turret(String name, int x, int y, float rotationAngle, int health, int damage, int range, int fireRate, int id, int targetEnemyId, Texture texture, BulletType bulletType) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.rotationAngle = rotationAngle;
        this.health = health;
        this.damage = damage;
        this.range = range;
        this.id = id;
        this.targetEnemyId = targetEnemyId;
        this.targetEnemy = Enemy.convertEnemyIdToEnemy(targetEnemyId);
        this.fireRate = fireRate;
        this.texture = texture;
        this.bulletType = bulletType;
    }

    public static void updateTextures() {
        basicTurretTexture = new Texture("basicTurret.png");
        plasmTurretTexture = new Texture("plasmTurret.png");
    }

    public static void setTurrets(List<Turret> turrets) {
        Turret.turrets = turrets;
    }

    public static void updateFontAndTexture() {
        turretBatch = new SpriteBatch();
    }

    public static void addTurretToList(List<Map<String, String>> turrets) {
        List<Turret> turretList = new ArrayList<>();
        for (Map<String, String> turretMap : turrets) {
            Texture texture;
            String name;
            BulletType bulletType;
            if (turretMap.get("texture").equals("basicTurret.png")) {
                texture = basicTurretTexture;
                name = "basicTurret";
                bulletType = BulletType.BULLET;
            } else {
                texture = plasmTurretTexture;
                name = "plasmTurret";
                bulletType = BulletType.BULLETBLUE;
            }
            turretList.add(new Turret(
                    name,
                    Integer.parseInt(turretMap.get("x")),
                    Integer.parseInt(turretMap.get("y")),
                    Float.parseFloat(turretMap.get("angle")),
                    Integer.parseInt(turretMap.get("health")),
                    Integer.parseInt(turretMap.get("damage")),
                    Integer.parseInt(turretMap.get("range")),
                    Integer.parseInt(turretMap.get("fireRate")),
                    Integer.parseInt(turretMap.get("id")),
                    Integer.parseInt(turretMap.get("targetEnemyId")),
                    texture,
                    bulletType
            ));
            Turret.setTurrets(turretList);
        }
    }

    /**
     * Register shoot.
     * @return bullet that was added.
     */
    public Bullet shootBullet() {
        Enemy target = targetEnemy;
        if (target == null) {
            return null;
        }
        if ((Math.abs(this.x - target.x) < range && Math.abs(this.y - target.y) < range) && globalTurretTimer % fireRate == 0) {
            Score.getInstance().addBulletFired();
            return new Bullet(this.x, this.y, (int) target.x - 32, (int) target.y - 40, -10000, 60, damage, bulletType);
        } else {
            return null;
        }
    }

    public static void render(OrthographicCamera camera, Player player) {
        incrementTimer();
        // Ask server for turrets
        PacketTurrets.askForTurrets(GameScreen.client);

        List<Turret> turretsToRender = new ArrayList<>(turrets);
        for (Turret turret : turretsToRender) {
            turretBatch.begin();
            turretBatch.setProjectionMatrix(camera.combined);
            if (Float.isNaN(turret.rotationAngle)) {
                turret.rotationAngle = 180;
            }
            turretBatch.draw(turret.texture, turret.x, turret.y, 32, 20, turret.texture.getWidth(), turret.texture.getHeight(), 1, 1, turret.rotationAngle, 0, 0, turret.texture.getWidth(), turret.texture.getHeight(), false, false);
            GraphicVariables.layout.setText(GraphicVariables.getGeneralFont(), turret.health + "hp");
            float textWidth = GraphicVariables.layout.width;
            GraphicVariables.getGeneralFont().draw(turretBatch, GraphicVariables.layout, turret.x - textWidth / 2 + 32, turret.y + turret.texture.getHeight() + 10);
            turretBatch.end();

            // damaging turret
            Rectangle botRectangle = new Rectangle(turret.x, turret.y, turret.texture.getWidth(), turret.texture.getHeight());
            for (Bullet bullet : Bullet.getBullets()) {
                if (botRectangle.contains(bullet.getX(), bullet.getY()) && bullet.getTTL() > 0 && bullet.getOwner() != -10000) {
                    bullet.deleteBullet();
                    PacketTurrets.damageTurret(turret.id, bullet.getDamage());
                    turret.health = turret.health - bullet.getDamage();
                    if (turret.health <= 0) {
                        turrets.remove(turret);
                    }
                }
            }

            Bullet turretBullet = turret.shootBullet();
            if (turretBullet != null) {
                Bullet.addBullet(turretBullet);
            }

        }
        turretsToRender.clear();
        turretsToRender = null;
    }

    public static void dispose() {
        for (Turret turret : turrets) {
            turret.texture.dispose();
        }
        turrets.clear();
    }

}
