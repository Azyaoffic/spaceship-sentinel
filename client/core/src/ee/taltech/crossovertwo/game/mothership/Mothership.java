package ee.taltech.crossovertwo.game.mothership;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import ee.taltech.crossovertwo.game.HUD.inventory.Inventory;
import ee.taltech.crossovertwo.game.HUD.messages.Message;
import ee.taltech.crossovertwo.game.HUD.messages.MsgType;
import ee.taltech.crossovertwo.game.bullet.Bullet;
import ee.taltech.crossovertwo.game.items.Item;
import ee.taltech.crossovertwo.game.items.heal.HealSelection;
import ee.taltech.crossovertwo.game.items.heal.ItemHeal;
import ee.taltech.crossovertwo.game.items.turret.ItemTurret;
import ee.taltech.crossovertwo.game.items.turret.TurretSelection;
import ee.taltech.crossovertwo.game.items.weapon.ItemWeapon;
import ee.taltech.crossovertwo.game.items.weapon.WeaponSelection;
import ee.taltech.crossovertwo.game.players.Player;
import ee.taltech.crossovertwo.packets.PacketGameEnd;
import ee.taltech.crossovertwo.packets.PacketMothership;
import ee.taltech.crossovertwo.utilities.ControlKeys;
import ee.taltech.crossovertwo.utilities.GraphicVariables;

import java.util.ArrayList;
import java.util.List;

import static ee.taltech.crossovertwo.game.players.Player.keyMap;

public class Mothership {
    private final int x = 1504; // mothership x coordinate
    private final int y = 1472; // mothership y coordinate
    private int width = 192; // mothership width
    private int height = 224;  // mothership height
    private int health = 10000;
    private final String name = "Mothership";
    private final SpriteBatch batch; // sprite batch for drawing

    /**
     * Constructor to initialize the Mothership
     */
    public Mothership() {
        batch = new SpriteBatch();
    }

    /**
     * This method renders the mothership
     * It also checks if the mothership is hit by a bullet
     * @param camera The camera to render the mothership on
     * @param player The player to check if the mothership is close enough to trade
     */
    public void render(OrthographicCamera camera, Player player) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        GraphicVariables.layout.setText(GraphicVariables.getGeneralFont(), this.getName());
        float textWidth = GraphicVariables.layout.width;
        GraphicVariables.getGeneralFont().draw(batch, GraphicVariables.layout, 1570 - textWidth / 2 + 32, 1600 + 140);
        GraphicVariables.layout.setText(GraphicVariables.getGeneralFont(), String.format("%04d", this.getHealth()) + "/10000");
        float textWidth1 = GraphicVariables.layout.width;
        GraphicVariables.getGeneralFont().draw(batch, GraphicVariables.layout, 1570 - textWidth1 / 2 + 32, 1600 + 120);
        if (Math.sqrt(Math.abs(x + width / 2 - player.getxCoord()) * Math.abs(x + width / 2 - player.getxCoord())
                + Math.abs(y + height / 2 - player.getyCoord()) * Math.abs(y + height / 2 - player.getyCoord())) < 300) {
            GraphicVariables.layout.setText(GraphicVariables.getGeneralFont(), "Press " + Input.Keys.toString(keyMap.get(ControlKeys.INVENTORY)) + " to trade!");
            float textWidth2 = GraphicVariables.layout.width;
            GraphicVariables.getGeneralFont().draw(batch, GraphicVariables.layout, 1570 - textWidth2 / 2 + 32, 1600 - 138);
            if (Player.inventory.getCurrentSource() != Inventory.Source.MOTHERSHIP) Player.inventory.setTrading(Inventory.Source.MOTHERSHIP);
        } else if (Player.inventory.getCurrentSource() != Inventory.Source.TRADER) {
            Player.inventory.setNotTrading();
        }
        batch.end();
        damagingMotherShip();
    }

    /**
     * This method checks if the mothership is hit by a bullet
     */
    public void damagingMotherShip() {
        Rectangle mothershipRectangle = new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        for (Bullet bullet : Bullet.getBullets()) {
            if (mothershipRectangle.contains(bullet.getX(), bullet.getY()) && bullet.getTTL() > 0 && bullet.getOwner() == -10) {
                System.out.println("Mothership hit");
                Message.showMessage("YOUR LOVELY MOTHERSHIP IS UNDER ATTACK!", MsgType.WARNING);
                this.damageMotherShip(100);
                PacketMothership.sendMothershipHp(health);
                bullet.deleteBullet();
            }
        }
    }

    /**
     * This method reduces the health of the mothership by the given damage
     * It also sets the gameover flag to true if the health is less than or equal to 0
     * @param damage The damage to be dealt
     */
    public void damageMotherShip(int damage) {
        if (health > 0) {
            health -= damage;
        }
        if (health <= 0) {
            health = 0;
            PacketGameEnd.sendGameEnd();
        }
    }

    /**
     * Heal mothership
     * @param hpToAdd Amount of hp to add
     */
    public void healMothership(int hpToAdd) {
        this.health += hpToAdd;
        if (this.health > 10000) {
            this.health = 10000;
        }
        PacketMothership.sendMothershipHeal(health);
    }

    /**
     * Generate trade items
     * @return List of items
     */
    public static List<Item> generateTradeItems() {
        List<Item> tradeItems = new ArrayList<>();
        tradeItems.add(new ItemWeapon(WeaponSelection.RIFLE));
        tradeItems.add(new ItemHeal(HealSelection.HEAL));
        tradeItems.add(new ItemTurret(TurretSelection.TURRET));
        tradeItems.add(new ItemHeal(HealSelection.BEER));
        return tradeItems;
    }

    /**
     * Set health
     * @param health Health to set
     */
    public void setHealth(int health) {
        this.health = health;
    }

    /**
     * This method returns the mothership's x coordinate
     * @return The mothership's x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * This method returns the mothership's y coordinate
     * @return The mothership's y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * This method returns the mothership's name
     * @return The mothership's name
     */
    public String getName() {
        return name;
    }

    /**
     * This method returns the mothership's width
     * @return The mothership's width
     */
    public int getWidth() {
        return width;
    }

    /**
     * This method returns the mothership's height
     * @return The mothership's height
     */
    public int getHeight() {
        return height;
    }

    /**
     * This method returns the mothership's health
     * @return The mothership's health
     */
    public int getHealth() {
        return health;
    }

    /**
     * This method disposes the mothership
     */
    public void dispose() {
        batch.dispose();
    }
}
