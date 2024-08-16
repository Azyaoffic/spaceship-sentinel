package ee.taltech.crossovertwo.game.items.turret;

import ee.taltech.crossovertwo.game.items.Item;

public enum TurretSelection {

    TURRET("Steelguard Sentry Turret",
            2,
            2,
            2,
            Item.Type.TURRET,
            1000,
            50,
            50,
            300,
            "basicTurret.png",
            "The turret is an automated defense system equipped with a rapid-firing ballistic weapon, providing reliable protection against enemy threats."),
    ENERGYTURRET("Luminex Energy Cannon",
            3,
            0,
            0,
            Item.Type.TURRET,
            1500,
            100,
            50,
            100,
            "plasmTurret.png",
            "The energy turret is a high-tech defensive weapon powered by advanced energy systems, emitting devastating beams to deter and eliminate enemy targets.");

    private final String name;
    private final int coal;
    private final int iron;
    private final int gold;
    private final Item.Type type;
    private final int turretHealth;
    private final int turretDamage;
    private final int turretRange;
    private final int turretFireRate;
    private final String turretImg;
    private final String description;

    /**
     * Constructor to initialize the TurretSelection
     * @param name The name of the turret
     * @param type The type of the turret
     * @param turretDamage The damage of the turret
     * @param turretRange The range of the turret
     * @param turretFireRate The fire rate of the turret
     * @param turretImg The image of the turret
     */
    TurretSelection(String name, int coal, int iron, int gold,
                    Item.Type type,
                    int turretHealth, int turretDamage, int turretRange, int turretFireRate,
                    String turretImg, String description) {
        this.name = name;
        this.type = type;
        this.turretHealth = turretHealth;
        this.turretDamage = turretDamage;
        this.turretRange = turretRange;
        this.turretFireRate = turretFireRate;
        this.turretImg = turretImg;
        this.description = description;
        this.coal = coal;
        this.iron = iron;
        this.gold = gold;
    }

    /**
     * This method returns the name of the turret
     * @return The name of the turret
     */
    public String getName() {
        return name;
    }

    /**
     * This method returns the coal cost of the turret
     * @return The coal cost of the turret
     */
    public int getCoal() {
        return coal;
    }

    /**
     * This method returns the iron cost of the turret
     * @return The iron cost of the turret
     */
    public int getIron() {
        return iron;
    }

    /**
     * This method returns the gold cost of the turret
     * @return The gold cost of the turret
     */
    public int getGold() {
        return gold;
    }

    /**
     * This method returns the type of the turret
     * @return The type of the turret
     */
    public Item.Type getType() {
        return type;
    }

    /**
     * This method returns the health of the turret
     * @return The health of the turret
     */
    public int getTurretHealth() {
        return turretHealth;
    }

    /**
     * This method returns the damage of the turret
     * @return The damage of the turret
     */
    public int getTurretDamage() {
        return turretDamage;
    }

    /**
     * This method returns the range of the turret
     * @return The range of the turret
     */
    public int getTurretRange() {
        return turretRange;
    }

    /**
     * This method returns the fire rate of the turret
     * @return The fire rate of the turret
     */
    public int getTurretFireRate() {
        return turretFireRate;
    }

    /**
     * This method returns the image of the turret
     * @return The image of the turret
     */
    public String getTurretImg() {
        return turretImg;
    }

    /**
     * This method returns the description of the turret
     * @return The description of the turret
     */
    public String getDescription() {
        return description;
    }
}
