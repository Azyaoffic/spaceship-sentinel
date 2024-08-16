package ee.taltech.crossovertwo.game.items.heal;

import ee.taltech.crossovertwo.game.items.Item;
import ee.taltech.crossovertwo.game.players.Player;

public class ItemHeal extends Item {

    private final int healAmount;

    /**
     * Constructor to initialize the ItemHeal
     * @param name The name of the item
     * @param cost The cost of the item
     * @param healAmount The amount of health the item heals
     * @param type The type of the item
     */
    public ItemHeal(String name, int cost, int healAmount, Item.Type type) {
        super(name, cost, type);
        this.healAmount = healAmount;
    }

    /**
     * Constructor to initialize the ItemHeal
     * @param healSelection The HealSelection to initialize the item
     */
    public ItemHeal(HealSelection healSelection) {
        this(healSelection.getName(),
                healSelection.getCost(),
                healSelection.getHealAmount(),
                healSelection.getType());
        this.setDescription(healSelection.getDescription());
    }

    @Override
    public void use() {
        Player.inventory.removeItem(this);
        if (getType().equals(Type.BEER)) {
            Player.drinkBeer();
            return;
        }
        Player.healPlayer(healAmount);
    }
}
