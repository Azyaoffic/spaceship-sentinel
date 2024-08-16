package ee.taltech.crossovertwo.game.items.bot;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import ee.taltech.crossovertwo.game.GameScreen;
import ee.taltech.crossovertwo.game.bullet.Bullet;
import ee.taltech.crossovertwo.game.enemies.Enemy;
import ee.taltech.crossovertwo.game.items.weapon.BulletType;
import ee.taltech.crossovertwo.game.players.Player;
import ee.taltech.crossovertwo.packets.PacketBot;
import ee.taltech.crossovertwo.utilities.GraphicVariables;

import java.util.ArrayList;
import java.util.List;

public class BotPlayer {
    private static int globalBotTimer = 1;

    public int health;
    public int damage;
    public int x;
    public int y;
    public int speed;
    public int targetEnemyId;
    private Enemy targetEnemy;
    private static Texture texture = new Texture("other.png");
    private static SpriteBatch batch = new SpriteBatch();
    public static List<BotPlayer> botPlayers = new ArrayList<>();

    /**
     * Constructor for the BotPlayer
     * @param health The health of the bot
     * @param damage The damage of the bot
     * @param speed The speed of the bot
     * @param x The x coordinate of the bot
     * @param y The y coordinate of the bot
     * @param targetEnemyId The id of the target enemy
     */
    public BotPlayer(int health, int damage, int speed, int x, int y, int targetEnemyId) {
        this.health = health;
        this.damage = damage;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.targetEnemyId = targetEnemyId;
        this.targetEnemy = Enemy.convertEnemyIdToEnemy(targetEnemyId);
    }

    /**
     * Increment timer.
     */
    private static void incrementTimer() {
        globalBotTimer++;
    }

    /**
     * Set list of bot players.
     */
    public static void setBotPlayers(List<BotPlayer> botPlayers) {
        BotPlayer.botPlayers = botPlayers;
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

        if ((Math.abs(this.x - target.x) < 400 && Math.abs(this.y - target.y) < 400) && BotPlayer.globalBotTimer % 60 == 0) {
            return new Bullet(this.x, this.y, (int) target.x - 32, (int) target.y - 40, -1000, 60, damage, BulletType.BULLET);
        } else {
            return null;
        }
    }

    /**
     * Render all enemies and calculate their damage and collision.
     * @param camera of game screen
     * @param player
     */
    public static void render(OrthographicCamera camera, Player player) {
        incrementTimer();
        // Ask server for bots
        PacketBot.askForBots(GameScreen.client);

        // Calculate everything regarding bots
        List<BotPlayer> botsToRender = new ArrayList<>(botPlayers);
        for (BotPlayer bot : botsToRender) {
            System.out.println("Bot: " + bot);

            // rendering alive bot
            batch.begin();
            batch.setProjectionMatrix(camera.combined);

            // angle between bot and enemy
            float rotation = 0f;
            if (bot.targetEnemyId != -1 && !Enemy.enemies.isEmpty() && bot.targetEnemy != null) {
                rotation = (float) Math.toDegrees(Math.atan2(bot.targetEnemy.y - bot.y - 40, bot.targetEnemy.x - bot.x - 32)) - 90;
            }

            batch.draw(texture, bot.x, bot.y, 16, 10, texture.getWidth(), texture.getHeight(), 1, 1, rotation, 0, 0, texture.getWidth(), texture.getHeight(), false, false);

            GraphicVariables.layout.setText(GraphicVariables.getGeneralFont(), bot.health + "hp");
            float textWidth = GraphicVariables.layout.width;
            GraphicVariables.getGeneralFont().draw(batch, GraphicVariables.layout, (int) bot.x - textWidth / 2 + 32, (int) bot.y + texture.getHeight() + 10);
            GraphicVariables.layout.setText(GraphicVariables.getGeneralFont(), "BOT");
            float textWidth1 = GraphicVariables.layout.width;
            GraphicVariables.getGeneralFont().draw(batch, GraphicVariables.layout, (int) bot.x - textWidth1 / 2 + 32, (int) bot.y + texture.getHeight() + 30);
            batch.end();

            // damaging bot
            Rectangle botRectangle = new Rectangle(bot.x, bot.y, texture.getWidth(), texture.getHeight());
            for (Bullet bullet : Bullet.getBullets()) {
                if (botRectangle.contains(bullet.getX(), bullet.getY()) && bullet.getTTL() > 0 && bullet.getOwner() != -1000) {
                    PacketBot.damageBot(bullet.getDamage());
                    bullet.deleteBullet();
                    if (bot.health <= 0) {
                        botPlayers.remove(bot);
                        PacketBot.sendDestructionPacket();
                    }
                }
            }

            Bullet enemyBullet = bot.shootBullet();
            if (enemyBullet != null) {
                Bullet.addBullet(enemyBullet);
            }

        }
        botsToRender.clear();
        botsToRender = null;
    }
}
