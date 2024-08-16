package ee.taltech.crossovertwo.game.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Client;
import ee.taltech.crossovertwo.Score;
import ee.taltech.crossovertwo.game.GameScreen;
import ee.taltech.crossovertwo.game.HUD.hud.HUD;
import ee.taltech.crossovertwo.game.HUD.ingamemenu.InGameMenu;
import ee.taltech.crossovertwo.game.HUD.inventory.CraftingTable;
import ee.taltech.crossovertwo.game.HUD.inventory.Inventory;
import ee.taltech.crossovertwo.game.HUD.messages.Message;
import ee.taltech.crossovertwo.game.HUD.messages.MsgType;
import ee.taltech.crossovertwo.game.bullet.Bullet;
import ee.taltech.crossovertwo.game.items.Item;
import ee.taltech.crossovertwo.game.items.bot.BotSelection;
import ee.taltech.crossovertwo.game.items.bot.ItemBot;
import ee.taltech.crossovertwo.game.items.heal.HealSelection;
import ee.taltech.crossovertwo.game.items.weapon.ItemWeapon;
import ee.taltech.crossovertwo.game.items.weapon.WeaponSelection;
import ee.taltech.crossovertwo.game.mothership.Mothership;
import ee.taltech.crossovertwo.game.resources.Resource;
import ee.taltech.crossovertwo.packets.PacketEnemies;
import ee.taltech.crossovertwo.packets.PacketGameEnd;
import ee.taltech.crossovertwo.packets.PacketPlayerWeapon;
import ee.taltech.crossovertwo.utilities.ControlKeys;
import ee.taltech.crossovertwo.utilities.PreferencesSaver;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.badlogic.gdx.math.MathUtils.random;
import static ee.taltech.crossovertwo.game.GameScreen.WORLD_HEIGHT;
import static ee.taltech.crossovertwo.game.GameScreen.WORLD_WIDTH;
import static ee.taltech.crossovertwo.packets.PacketBullets.sendBullets;
import static ee.taltech.crossovertwo.packets.PacketPlayersCoordinates.*;

public class Player {

    // Player variables
    public static final int HP = 300;
    private static String nickname;
    private static int hp;
    public static final int PLAYER_SIZE = 40;
    private static final int SPEED = 300;
    // Random coordinates
    private int xCoord;
    private int yCoord;

    private final SpriteBatch batch;
    private final Client client; // Should be declared in Packet then it is not needed here
    public OrthographicCamera camera;
    private final int clientId;
    private final Array<RectangleMapObject> worldObjects;
    private float savedRotationAngle;
    public static Inventory inventory;
    private final Mothership mothership;
    public static ItemWeapon mainWeapon;
    private Texture imgToShow;
    private final Texture img = new Texture("player.png");
    private final Texture imgDead = new Texture("playerDead.png");

    private long lastShootTime = 0;
    private static boolean isAlive = true;

    // Resources
    private static int coal = 0;
    private static int iron = 0;
    private static int gold = 0;

    // Keys
    public static Map<ControlKeys, Integer> keyMap;

    /**
     * Method to get the amount of a resource
     * @param resource The resource to get the amount of
     * @return The amount of the resource
     */
    public static int getResourceAmount(String resource) {
        return switch (resource) {
            case "coal" -> coal;
            case "iron" -> iron;
            case "gold" -> gold;
            default -> 0;
        };
    }

    /**
     * Method to clear the player's resources
     */
    public static void clearPlayerResources() {
        coal = 0;
        iron = 0;
        gold = 0;
    }

    /**
     * Method to add resources to the player
     * @param resources List of resources to be added
     */
    public void addResource(List<Resource> resources) {
        if (!resources.isEmpty()) {
            Resource resource = resources.getFirst();
            switch (resource.getName()) {
                case "coal":
                    coal += resources.size();
                    break;
                case "iron":
                    iron += resources.size();
                    break;
                case "gold":
                    gold += resources.size();
                    break;
            }
            Message.showMessage(String.format("Received %s %s, total %s: %s",
                    resources.size(), resource.getName(), resource.getName(), getResourceAmount(resource.getName())), MsgType.INFO);
        }
    }

    /**
     * Method to remove resources from the player
     * @param amountCoal Amount of coal to be removed
     * @param amountIron Amount of iron to be removed
     * @param amountGold Amount of gold to be removed
     */
    public static void removeResource(int amountCoal, int amountIron, int amountGold) {
        coal -= amountCoal;
        iron -= amountIron;
        gold -= amountGold;
    }

    /**
     * Constructor to initialize the Player
     * @param client The client instance
     */
    public Player(Client client, Array<RectangleMapObject> worldObjects, String nickname, Mothership mothership) {
        this.keyMap = PreferencesSaver.loadAllPreferences();
        this.worldObjects = worldObjects;
        this.mothership = mothership;
        this.batch = new SpriteBatch();
        this.client = client;
        this.nickname = nickname;
        this.imgToShow = img;
        Player.isAlive = true;
        hp = HP;

        worldObjects.add(new RectangleMapObject(mothership.getX(), mothership.getY(), mothership.getWidth(), mothership.getHeight()));

        System.out.println(nickname);
        // Set up initial player coordinates, angle and nickname
        generateInitialCoords();
        sendFullPacket(xCoord, yCoord, 0f);
        System.out.println("Full packet sent! " + xCoord + " " + yCoord + " " + nickname);

        cameraSetup();

        clientId = client.getID();
        System.out.println("Client ID: " + clientId);

    }

    /**
     * Method to set up the camera according to the player's position.
     */
    private void cameraSetup() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(xCoord, yCoord, 0);
        camera.update();
    }

    /**
     * Method to generate initial coordinates for the player.
     * Initial coordinates are generated randomly
     * until the player is not colliding with any world objects.
     */
    private void generateInitialCoords() {
        xCoord = random.nextInt(1280, 1920);
        yCoord = random.nextInt(1280, 1920);
        while (isCollidingWithWorldObjects(xCoord, yCoord)) {
            xCoord = random.nextInt(1280, 1920);
            yCoord = random.nextInt(1280, 1920);
        }
    }

    /**
     * Method to create the inventory.
     * Also creates the main weapon for the player.
     */
    public void createInventory() {
        inventory = new Inventory();
        ItemWeapon pistol = new ItemWeapon(WeaponSelection.PISTOL);
        inventory.addItem(pistol);
    }

    /**
     * Check if the player is colliding with any world objects
     * @param x x coordinate
     * @param y y coordinate
     * @return true if the player is colliding with any world objects, false otherwise
     */
    private boolean isCollidingWithWorldObjects(int x, int y) {
        if (!isAlive) return false;
        for (RectangleMapObject object : worldObjects) {
            float objectX = object.getRectangle().getX();
            float objectY = object.getRectangle().getY();
            float objectWidth = object.getRectangle().getWidth();
            float objectHeight = object.getRectangle().getHeight();

            if (x + 40 > objectX && x < objectX + objectWidth &&
                    y + 40 > objectY && y < objectY + objectHeight) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to damage player
     * @param damage Damage to be dealt
     */
    public static void damagePlayer(int damage) {
        if (hp > 0) {
            hp -= damage;
            if (hp <= 0) {
                hp = 0;
            }
        }
    }

    /**
     * Method to heal (or damage) player with beer
     */
    public static void drinkBeer() {
        if (hp < HP / 2) {
            hp -= HealSelection.BEER.getHealAmount();
        } else {
            hp += HealSelection.BEER.getHealAmount();
        }
    }

    /**
     * Method to heal player
     * @param heal Amount of health to be healed
     */
    public static void healPlayer(int heal) {
        if (HP == hp) Message.showMessage("You are at full health lol, just failed the heal");
        if (hp <= 0) Message.showMessage("You are dead, you can't heal");
        Score.getInstance().addHpHealed(heal);
        hp += heal;
        Message.showMessage("You have been healed for " + heal + " HP");
        if (hp > HP) hp = HP;
    }

    /**
     * Method to control the player supports movement and shooting
     * @param rotationAngle The rotation angle of the player that we received in draw method
     */
    private void playerControls(float rotationAngle) {
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(keyMap.get(ControlKeys.UP))) {
            // Check for collision with world objects before moving up
            if (!isCollidingWithWorldObjects(xCoord, yCoord + (int) (SPEED * Gdx.graphics.getDeltaTime()))) {
                yCoord += (int) (SPEED * Gdx.graphics.getDeltaTime());
                sendCoords(xCoord, yCoord, savedRotationAngle);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(keyMap.get(ControlKeys.DOWN))) {
            // Check for collision with world objects before moving down
            if (!isCollidingWithWorldObjects(xCoord, yCoord - (int) (SPEED * Gdx.graphics.getDeltaTime()))) {
                yCoord -= (int) (SPEED * Gdx.graphics.getDeltaTime());
                sendCoords(xCoord, yCoord, savedRotationAngle);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(keyMap.get(ControlKeys.LEFT))) {
            // Check for collision with world objects before moving left
            if (!isCollidingWithWorldObjects(xCoord - (int) (SPEED * Gdx.graphics.getDeltaTime()), yCoord)) {
                xCoord -= (int) (SPEED * Gdx.graphics.getDeltaTime());
                sendCoords(xCoord, yCoord, savedRotationAngle);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(keyMap.get(ControlKeys.RIGHT))) {
            // Check for collision with world objects before moving right
            if (!isCollidingWithWorldObjects(xCoord + (int) (SPEED * Gdx.graphics.getDeltaTime()), yCoord)) {
                xCoord += (int) (SPEED * Gdx.graphics.getDeltaTime());
                sendCoords(xCoord, yCoord, savedRotationAngle);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            // Debug button for spawning enemy
            PacketEnemies.createWave(client ,10);
            hp = 1000000000;
            mothership.healMothership(1000000);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.HOME)) {
            // Debug button for testing bots
            inventory.addMoney(1000000);
            inventory.addItem(new ItemBot(BotSelection.DAMAGER));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            // Debug button for random tp
            this.xCoord = random(0, WORLD_WIDTH);
            this.yCoord = random(0, WORLD_HEIGHT);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (inventory.isVisible()) inventory.show();
            CraftingTable.show(false);
            InGameMenu.show();
        }

        if (!isAlive) return;

        // Bullets
        if (Gdx.input.isButtonJustPressed(keyMap.get(ControlKeys.SHOOT)) && !inventory.isVisible() && !InGameMenu.isMenuVisible() && !CraftingTable.isCrafting() && mainWeapon != null) {
            // shooting with delay
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShootTime > mainWeapon.getWeaponFireRate()) {
                lastShootTime = currentTime;
                Score.getInstance().addBulletFired();
                Bullet.addBullet(new Bullet(xCoord, yCoord, rotationAngle, 1, mainWeapon.getWeaponRange(), mainWeapon.getWeaponDamage(), mainWeapon.getBullet()));
                sendBullets(xCoord, yCoord, rotationAngle, mainWeapon.getBullet());
            }
        }

        // Inventory
        if (Gdx.input.isKeyJustPressed(keyMap.get(ControlKeys.INVENTORY))) {
            if (InGameMenu.isMenuVisible()) InGameMenu.show();
            inventory.show();
        }

        if (Gdx.input.isKeyJustPressed(keyMap.get(ControlKeys.CRAFT))) {
            inventory.switchCrafting();
        }

        // Switching weapons
        if (Gdx.input.isKeyJustPressed(keyMap.get(ControlKeys.UNARMED))) {
            mainWeapon = null;
            PacketPlayerWeapon.sendPlayerWeapon(null);
        } else if (Gdx.input.isKeyJustPressed(keyMap.get(ControlKeys.WEAPON))) {
            if (!inventory.getItems().stream().filter(item -> item.getType() == Item.Type.WEAPON).toList().isEmpty()) {
                inventory.getItems().stream().filter(item -> item.getType() == Item.Type.WEAPON).findFirst().ifPresent(item -> {
                    inventory.setSelectedItem(item);
                    inventory.useSelectedItem();
                });
            } else {
                Message.showMessage("There are no weapons in your inventory");
            }
        } else if (Gdx.input.isKeyJustPressed(keyMap.get(ControlKeys.HEAL))) {
            if (!inventory.getItems().stream().filter(item -> item.getType() == Item.Type.HEAL).toList().isEmpty()) {
                inventory.getItems().stream().filter(item -> item.getType() == Item.Type.HEAL).findFirst().ifPresent(item -> {
                    inventory.setSelectedItem(item);
                    inventory.useSelectedItem();
                });
            } else {
                Message.showMessage("There are no healing items in your inventory");
            }
        } else if (Gdx.input.isKeyJustPressed(keyMap.get(ControlKeys.TURRET))) {
            if (!inventory.getItems().stream().filter(item -> item.getType() == Item.Type.TURRET).toList().isEmpty()) {
                inventory.getItems().stream().filter(item -> item.getType() == Item.Type.TURRET).findFirst().ifPresent(item -> {
                    inventory.setSelectedItem(item);
                    inventory.useSelectedItem();
                });
            } else {
                Message.showMessage("There are no turrets in your inventory");
            }
        }

    }

    /**
     * Method to draw the player
     */
    public void draw(OrthographicCamera camera) {
        batch.setProjectionMatrix(camera.combined); // Set the batch's projection matrix to match the camera's
        batch.begin();

        // Timer
        if (GameScreen.serverTime != null) {
            // System.out.println(GameScreen.serverTime);
            long millis = Long.parseLong(GameScreen.serverTime.get(-10).get("endTime")) - Long.parseLong(GameScreen.serverTime.get(-10).get("currentTime"));
            String timeLeft = TimeUnit.MILLISECONDS.toMinutes(millis) + ":" + String.format("%02d",(TimeUnit.MILLISECONDS.toSeconds(millis) % 60));
            HUD.updateTime(timeLeft);
        }

        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();
        float rotationAngle = (float) -Math.toDegrees(Math.atan2(((double) Gdx.graphics.getHeight() / 2 - mouseY), ((double) Gdx.graphics.getWidth() / 2 - mouseX))) + 90;

        // Checks delta angle and sets the rotation angle and then sends
        if (Math.abs(savedRotationAngle - rotationAngle) > 0.1) {
            savedRotationAngle = rotationAngle;
            sendCoords(xCoord, yCoord, savedRotationAngle);
        }

        PlayerOther.receivedPlayerCoordinates.put(clientId, new int[]{xCoord, yCoord});

        // draw player
        if (mainWeapon != null) {
            batch.draw(mainWeapon.getPlayerWeaponImg(), xCoord, yCoord, 32, 16, 64, 64, 1, 1, rotationAngle, 0, 0, 64, 64, false, false);
        } else {
            batch.draw(imgToShow, xCoord, yCoord, 32, 16, 64, 64, 1, 1, rotationAngle, 0, 0, 64, 64, false, false);
        }
        // draw player's nickname and health
        HUD.updateHealth(hp);

        batch.end();

        playerControls(rotationAngle);

        // Damaging player
        Rectangle playerRectange = new Rectangle(this.xCoord, this.yCoord - 10, PLAYER_SIZE, PLAYER_SIZE);
        for (Bullet bullet : Bullet.getBullets()) {
            if (playerRectange.contains(bullet.getX(), bullet.getY()) && bullet.getTTL() > 0 && bullet.getOwner() != 1) {
                damagePlayer(10);
                bullet.deleteBullet();
                if (hp <= 0) {
                    playerKilled();
                }
            }
        }

        camera.position.set(xCoord + 20, yCoord + 20, 0);
        camera.update();
    }

    /**
     * When the player is dead his properties should be reset
     * Or the game should be ended if no players are alive
     */
    public void playerKilled() {
        isAlive = false;
        sendChangeStatus(isAlive);
        mainWeapon = null;
        imgToShow = imgDead;
        Message.showMessage("YOU DIED", MsgType.ERROR);

        Score.getInstance().addDeath();

        inventory.show(false);
        InGameMenu.show(false);

        for (Map.Entry<Integer, Map<String, String>> entry : PlayerOther.receivedPlayersStatus.entrySet()) {
            if (entry.getKey() == clientId) {
                continue;
            }
            if (Boolean.parseBoolean(entry.getValue().get("status"))) {
                return;
            }
        }
        // If no players are alive, the game should be ended
        PacketGameEnd.sendGameEnd();
    }

    /**
     * Method to revive the player
     */
    public void revive() {
        if (isAlive) return;
        isAlive = true;
        if (isCollidingWithWorldObjects(xCoord, yCoord)) {
            generateInitialCoords();
            sendCoords(xCoord, yCoord, savedRotationAngle);
        }
        sendChangeStatus(isAlive);
        hp = HP / 2;
        imgToShow = img;

        Message.showMessage("You have been revived", MsgType.INFO);
    }

    /**
     * Method to dispose of the player
     */
    public void dispose() {
        imgToShow.dispose();
        imgDead.dispose();
        batch.dispose();
        mainWeapon = null;
    }

    /**
     * Set the main weapon of the player
     * @param mainWeapon The main weapon to set
     */
    public static void setMainWeapon(ItemWeapon mainWeapon) {
        Player.mainWeapon = mainWeapon;
    }

    /**
     * Get x coordinate of the player
     * @return x coordinate of the player
     */
    public int getxCoord() {
        return xCoord;
    }

    /**
     * Get y coordinate of the player
     * @return y coordinate of the player
     */
    public int getyCoord() {
        return yCoord;
    }

    /**
     * Get the nickname of the player
     * @return nickname of the player
     */
    public static String getNickname() {
        return nickname;
    }

    /**
     * Get the hp of the player
     * @return hp of the player
     */
    public static int getHp() {
        return hp;
    }

    /**
     * Get main weapon of the player
     * @return main weapon of the player
     */
    public static ItemWeapon getMainWeapon() {
        return mainWeapon;
    }
}
