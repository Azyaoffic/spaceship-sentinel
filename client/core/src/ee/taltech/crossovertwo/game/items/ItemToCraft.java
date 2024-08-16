package ee.taltech.crossovertwo.game.items;

public class ItemToCraft extends Item {

    private final int coal, iron, gold;

    /**
     * Constructor to initialize the Item
     *
     * @param name The name of the item
     * @param type The type of the item
     */
    public ItemToCraft(String name, Type type, int coal, int iron, int gold) {
        super(name, 100, type);
        this.coal = coal;
        this.iron = iron;
        this.gold = gold;
    }

    /**
     * This method returns the coal cost of the turret
     *
     * @return The coal cost of the turret
     */
    public int getCoal() {
        return coal;
    }

    /**
     * This method returns the iron cost of the turret
     *
     * @return The iron cost of the turret
     */
    public int getIron() {
        return iron;
    }

    /**
     * This method returns the gold cost of the turret
     *
     * @return The gold cost of the turret
     */
    public int getGold() {
        return gold;
    }
}
