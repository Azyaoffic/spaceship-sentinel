package ee.taltech.crossovertwo.game.items.bot;

import ee.taltech.crossovertwo.game.items.Item;
import ee.taltech.crossovertwo.game.players.Player;
import ee.taltech.crossovertwo.packets.PacketBot;

public class ItemBot extends Item {
    private final int health;
    private final int damage;

    /**
     * Constructor to initialize the ItemBot
     * @param name The name of the item
     * @param cost The cost of the item
     * @param health Amount of health bot has
     * @param damage Amount of damage bot deals
     * @param type The type of the item
     */
    public ItemBot(String name, int cost, int health, int damage, Type type) {
        super(name, cost, type);
        this.health = health;
        this.damage = damage;
    }

    /**
     * Constructor to initialize the ItemHeal
     * @param botSelection the botSelection to initialize the item
     */
    public ItemBot(BotSelection botSelection) {
        this(botSelection.getName(),
                botSelection.getCost(),
                botSelection.getHealth(),
                botSelection.getDamage(),
                botSelection.getType());
    }

    @Override
    public void use() {
        PacketBot.askForBot(this.getName());
        Player.inventory.removeItem(this);
    }
}
