package ee.taltech.crossovertwo.game.items.heal;

import ee.taltech.crossovertwo.game.items.Item;

public enum HealSelection {

    HEAL("Heal Potion",
            100,
            30,
            Item.Type.HEAL,
            "This medical kit, infused with advanced nanotech, swiftly repairs moderate damage to you during battles with monstrous foes."),
    HEALPLUS("Heal Potion +",
            200,
            60,
            Item.Type.HEAL,
            "The Heal+ Item, a pinnacle of healing tech, emits a pulsating glow as it rapidly restores a significant amount of your health."),
    BEER("BEER",
            10,
            -30,
            Item.Type.BEER,
            "People get drunk on beer, but drown in water.");

    private final String name;
    private final int cost;
    private final Item.Type type;
    private final int healAmount;
    private String description;

    /**
     * Constructor for the heal selection
     * @param name The name of the heal
     * @param cost The cost of the heal
     * @param healAmount The amount of health the heal restores
     * @param type The type of the heal
     * @param description The description of the heal
     */
    HealSelection(String name, int cost, int healAmount, Item.Type type, String description) {
        this.name = name;
        this.cost = cost;
        this.healAmount = healAmount;
        this.type = type;
        this.description = description;
    }

    /**
     * This method returns the name of the heal
     * @return The name of the heal
     */
    public String getName() {
        return name;
    }

    /**
     * This method returns the cost of the heal
     * @return The cost of the heal
     */
    public int getCost() {
        return cost;
    }

    /**
     * This method returns the type of the heal
     * @return The type of the heal
     */
    public Item.Type getType() {
        return type;
    }

    /**
     * This method returns the amount of health the heal restores
     * @return The amount of health the heal restores
     */
    public int getHealAmount() {
        return healAmount;
    }

    /**
     * This method returns the description of the heal
     * @return The description of the heal
     */
    public String getDescription() {
        return description;
    }
}
