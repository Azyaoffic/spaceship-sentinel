package ee.taltech.crossovertwo.game.items.bot;

import ee.taltech.crossovertwo.game.items.Item;

public enum BotSelection {

    TANK("Tanky Bot", 500, 400, 25, Item.Type.BOT),
    DAMAGER("Damaging Bot", 500, 200, 50, Item.Type.BOT),
    WEAKLING("Weakling", 300, 200, 25, Item.Type.BOT);

    private final String name;
    private final int cost;
    private final Item.Type type;
    private final int health;
    private final int damage;

    /**
     * Constructor for the bot selection
     * @param name The name of the bot
     * @param cost The cost of the bot
     * @param health The health of the bot
     * @param damage The damage of the bot
     * @param type The type of the bot
     */
    BotSelection(String name, int cost, int health, int damage, Item.Type type) {
        this.name = name;
        this.cost = cost;
        this.health = health;
        this.damage = damage;
        this.type = type;
    }

    /**
     * This method returns the name of the bot
     * @return The name of the bot
     */
    public String getName() {
        return name;
    }

    /**
     * This method returns the cost of the bot
     * @return The cost of the bot
     */
    public int getCost() {
        return cost;
    }

    /**
     * This method returns the type of the bot
     * @return The type of the bot
     */
    public Item.Type getType() {
        return type;
    }

    /**
     * This method returns the health of the bot
     * @return The health of the bot
     */
    public int getHealth() {
        return health;
    }

    /**
     * This method returns the damage of the bot
     * @return The damage of the bot
     */
    public int getDamage() {
        return damage;
    }

}
