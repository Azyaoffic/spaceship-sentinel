package ee.taltech.crossovertwo.game.items.weapon;

import com.badlogic.gdx.graphics.Texture;
import ee.taltech.crossovertwo.game.items.Item;

public class ItemWeapon extends Item {
    protected int weaponDamage;
    protected int weaponRange;
    protected int weaponFireRate;
    protected Texture playerWeaponImg;
    protected Texture otherWeaponImg;
    protected BulletType bullet;

    /**
     * Constructor to initialize the ItemWeapon
     * @param name The name of the weapon
     * @param cost The cost of the weapon
     * @param type The type of the weapon
     * @param weaponDamage The damage of the weapon
     * @param weaponRange The range of the weapon
     * @param weaponFireRate The fire rate of the weapon
     * @param playerWeaponImg The image of the player weapon
     * @param otherWeaponImg The image of the other player weapon
     * @param bullet The bullet type of the weapon
     */
    public ItemWeapon(String name, int cost, Type type,
                      int weaponDamage, int weaponRange, int weaponFireRate,
                      Texture playerWeaponImg, Texture otherWeaponImg, BulletType bullet) {
        super(name, cost, type);
        this.weaponDamage = weaponDamage;
        this.weaponRange = weaponRange;
        this.weaponFireRate = weaponFireRate;
        this.playerWeaponImg = playerWeaponImg;
        this.otherWeaponImg = otherWeaponImg;
        this.bullet = bullet;
    }

    /**
     * Constructor to initialize the ItemWeapon
     * @param weaponSelection The WeaponSelection to initialize the item
     */
    public ItemWeapon(WeaponSelection weaponSelection) {
        this(weaponSelection.getName(),
                weaponSelection.getCost(),
                weaponSelection.getType(),
                weaponSelection.getWeaponDamage(),
                weaponSelection.getWeaponRange(),
                weaponSelection.getWeaponFireRate(),
                new Texture(weaponSelection.getPlayerWeaponImg()),
                new Texture(weaponSelection.getOtherWeaponImg()),
                weaponSelection.getBullet());
        this.setDescription(weaponSelection.getDescription());
    }

    /**
     * This method returns the damage of the weapon
     * @return The damage of the weapon
     */
    public int getWeaponDamage() {
        return weaponDamage;
    }

    /**
     * This method returns the range of the weapon
     * @return The range of the weapon
     */
    public int getWeaponRange() {
        return weaponRange;
    }

    /**
     * This method returns the fire rate of the weapon
     * @return The fire rate of the weapon
     */
    public int getWeaponFireRate() {
        return weaponFireRate;
    }

    /**
     * This method returns the image of the player weapon
     * @return The image of the player weapon
     */
    public Texture getPlayerWeaponImg() {
        return playerWeaponImg;
    }

    /**
     * This method returns the image of the other player weapon
     * @return The image of the other player weapon
     */
    public Texture getOtherWeaponImg() {
        return otherWeaponImg;
    }

    /**
     * This method returns the bullet type of the weapon
     * @return The bullet type of the weapon
     */
    public BulletType getBullet() {
        return bullet;
    }
}
