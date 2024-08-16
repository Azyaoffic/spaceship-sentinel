package ee.taltech.crossovertwo.game.items;

public abstract class Item {

    public enum Type {
        WEAPON, HEAL, UPGRADE, TURRET, BOT, BEER, REVIVER
    }

    private String name;
    private int cost;
    private Type type;
    private String description;

    /**
     * Constructor to initialize the Item
     * @param name The name of the item
     * @param cost The cost of the item
     * @param type The type of the item
     */
    public Item(String name, int cost, Type type) {
        this.type = type;
        this.name = name;
        this.cost = cost;
        this.description = "Standard description";
    }

    /**
     * This method is used to set the description of the item.
     * @param description The description of the item
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This method returns the name of the item
     * @return The name of the item
     */
    public String getName() {
        return name;
    }

    /**
     * This method returns the cost of the item
     * @return The cost of the item
     */
    public int getCost() {
        return cost;
    }

    /**
     * This method returns the type of the item
     * @return The type of the item
     */
    public Type getType() {
        return type;
    }

    /**
     * This method returns the description of the item
     * @return The description of the item
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method is used to use the item from the inventory.
     */
    public void use() { }

    @Override
    public String toString() {
        return name + " (" + cost + "$)";
    }
}
