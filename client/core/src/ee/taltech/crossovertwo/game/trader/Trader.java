package ee.taltech.crossovertwo.game.trader;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.utils.Array;
import ee.taltech.crossovertwo.game.GameScreen;
import ee.taltech.crossovertwo.game.HUD.inventory.Inventory;
import ee.taltech.crossovertwo.game.items.Item;
import ee.taltech.crossovertwo.game.items.bot.BotSelection;
import ee.taltech.crossovertwo.game.items.bot.ItemBot;
import ee.taltech.crossovertwo.game.items.heal.HealSelection;
import ee.taltech.crossovertwo.game.items.heal.ItemHeal;
import ee.taltech.crossovertwo.game.items.weapon.ItemWeapon;
import ee.taltech.crossovertwo.game.items.weapon.WeaponSelection;
import ee.taltech.crossovertwo.game.players.Player;
import ee.taltech.crossovertwo.utilities.ControlKeys;
import ee.taltech.crossovertwo.utilities.GraphicVariables;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static ee.taltech.crossovertwo.game.players.Player.keyMap;

public abstract class Trader {

    private static Array<RectangleMapObject> worldObjects;
    private static Integer xCoord, yCoord;
    public static boolean isTraderActive = false;
    private static final int WORLD_WIDTH = GameScreen.WORLD_WIDTH;
    private static final int WORLD_HEIGHT = GameScreen.WORLD_HEIGHT;
    private static final Texture texture = new Texture("WanderingTrader.png");
    private static Random random = new Random();
    private static SpriteBatch batch = new SpriteBatch();

    /**
     * Method to spawn the trader
     * @param worldObjects The world objects to check if the trader is colliding with any of them
     */
    public static void spawnTrader(Array<RectangleMapObject> worldObjects) {
        Trader.worldObjects = worldObjects;
        generateInitialCoords();
        isTraderActive = true;
    }

    /**
     * Method to render the trader
     * @param camera The camera to render the trader on
     * @param player The player to check if the trader is close enough to trade
     */
    public static void render(OrthographicCamera camera, Player player) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(texture, xCoord, yCoord);

        if (Math.sqrt(Math.abs(xCoord - player.getxCoord()) * Math.abs(xCoord - player.getxCoord())
                + Math.abs(yCoord - player.getyCoord()) * Math.abs(yCoord - player.getyCoord())) < 300) {
            GraphicVariables.layout.setText(GraphicVariables.getGeneralFont(), "Press " + Input.Keys.toString(keyMap.get(ControlKeys.INVENTORY)) + " to trade!");
            float textWidth = GraphicVariables.layout.width;
            GraphicVariables.getGeneralFont().draw(batch, GraphicVariables.layout, xCoord - textWidth / 2 + 32, yCoord);
            if (Player.inventory.getCurrentSource() != Inventory.Source.TRADER) Player.inventory.setTrading(Inventory.Source.TRADER);
        } else if (Player.inventory.getCurrentSource() != Inventory.Source.MOTHERSHIP) {
            Player.inventory.setNotTrading();
        }

        batch.end();
    }

    /**
     * Method to despawn the trader
     */
    public static void despawnTrader() {
        xCoord = yCoord = null;
        isTraderActive = false;
    }

    /**
     * Check if the player is colliding with any world objects
     * @param x x coordinate
     * @param y y coordinate
     * @return true if the player is colliding with any world objects, false otherwise
     */
    private static boolean isCollidingWithWorldObjects(int x, int y) {
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
     * Method to generate initial coordinates for the player.
     * Initial coordinates are generated randomly
     * until the player is not colliding with any world objects.
     */
    private static void generateInitialCoords() {
        xCoord = random.nextInt(WORLD_WIDTH);
        yCoord = random.nextInt(WORLD_HEIGHT);
        while (isCollidingWithWorldObjects(xCoord, yCoord)) {
            xCoord = random.nextInt(WORLD_WIDTH);
            yCoord = random.nextInt(WORLD_HEIGHT);
        }
    }

    /**
     * Generate trade items
     * @return List of items
     */
    public static List<Item> generateTradeItems() {
        List<Item> tradeItems = new ArrayList<>();
        tradeItems.add(new ItemHeal(HealSelection.HEALPLUS));
        tradeItems.add(new ItemWeapon(WeaponSelection.SHOTGUN));
        tradeItems.add(new ItemWeapon(WeaponSelection.ASSAULTRIFLE));

        Item botSummoner = new ItemBot(BotSelection.WEAKLING);
        int botToShow = random.nextInt(3);
        switch (botToShow) {
            case 1 -> botSummoner = new ItemBot(BotSelection.TANK);
            case 2 -> botSummoner = new ItemBot(BotSelection.DAMAGER);
            case 0 -> botSummoner = new ItemBot(BotSelection.WEAKLING);
        }

        tradeItems.add(botSummoner);

        return tradeItems;
    }
}
