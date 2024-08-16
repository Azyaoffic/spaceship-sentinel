package ee.taltech.crossovertwo.game.items.weapon;


import ee.taltech.crossovertwo.game.items.Item;

public enum WeaponSelection {

    RIFLE("AK74",
            700,
            Item.Type.WEAPON,
            100,
            60,
            300,
            "playerRifle.png",
            "otherRifle.png",
            "Originating from the 21st century, the AK74 is a durable assault rifle known for its ruggedness and effectiveness in various environments.",
            BulletType.BULLET),
    PISTOL("M11 Compact",
            200,
            Item.Type.WEAPON,
            50,
            30,
            600,
            "playerPistol.png",
            "otherPistol.png",
            "Hailing from the 21st century, the M11 Compact is a reliable semi-automatic pistol, favored for its maneuverability and precision in close-quarters combat.",
            BulletType.BULLET),

    ASSAULTRIFLE("NeuroBlade MK-X",
            1500,
            Item.Type.WEAPON,
            100,
            100,
            100,
            "playerLaserGun.png",
            "otherLaserGun.png",
            "The pinnacle of futuristic armaments, this weapon embodies cutting-edge technology, utilizing advanced energy systems to unleash devastating attacks with pinpoint precision.",
            BulletType.BULLETBLUE),
    SHOTGUN("Mossberg 500",
            500,
            Item.Type.WEAPON,
            150,
            10,
            600,
            "playerLaserPistol.png",
            "otherLaserPistol.png",
            "Energy-based shotgun, the Mossberg 500 is a powerful weapon that delivers devastating close-range attacks, capable of eliminating multiple (not yet) targets with a single shot.",
            BulletType.BULLETBLUE);


    private final String name;
    private final int cost;
    private final Item.Type type;
    private final int weaponDamage;
    private final int weaponRange;
    private final int weaponFireRate;
    private final String playerWeaponImg;
    private final String otherWeaponImg;
    private String description;
    private BulletType bullet;

    /**
     * Constructor to initialize the WeaponSelection
     * @param name The name of the weapon
     * @param cost The cost of the weapon
     * @param type The type of the weapon
     * @param weaponDamage The damage of the weapon
     * @param weaponRange The range of the weapon
     * @param weaponFireRate The fire rate of the weapon
     * @param playerWeaponImg The image of the player weapon
     * @param otherWeaponImg The image of the other player weapon
     */
    WeaponSelection(String name, int cost, Item.Type type,
                    int weaponDamage, int weaponRange, int weaponFireRate,
                    String playerWeaponImg, String otherWeaponImg, String description, BulletType bullet) {
        this.name = name;
        this.cost = cost;
        this.type = type;
        this.weaponDamage = weaponDamage;
        this.weaponRange = weaponRange;
        this.weaponFireRate = weaponFireRate;
        this.playerWeaponImg = playerWeaponImg;
        this.otherWeaponImg = otherWeaponImg;
        this.description = description;
        this.bullet = bullet;
    }

    /**
     * This method returns the name of the weapon
     * @return The name of the weapon
     */
    public String getName() {
        return name;
    }

    /**
     * This method returns the cost of the weapon
     * @return The cost of the weapon
     */
    public int getCost() {
        return cost;
    }

    /**
     * This method returns the type of the weapon
     * @return The type of the weapon
     */
    public Item.Type getType() {
        return type;
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
    public String getPlayerWeaponImg() {
        return playerWeaponImg;
    }

    /**
     * This method returns the image of the other player weapon
     * @return The image of the other player weapon
     */
    public String getOtherWeaponImg() {
        return otherWeaponImg;
    }

    /**
     * This method returns the description of the weapon
     * @return The description of the weapon
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method returns the bullet type of the weapon
     * @return The bullet type of the weapon
     */
    public BulletType getBullet() {
        return bullet;
    }

}
