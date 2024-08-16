package ee.taltech.crossovertwo.packets;

import com.esotericsoftware.kryonet.Client;

import java.util.HashMap;
import java.util.Map;

public class PacketBot extends Packet {

    /**
     * This method asks for a bot
     * @param botName The name of the bot
     */
    public static void askForBot(String botName) {
        Map<String, String> bot = new HashMap<>();
        bot.put("game", "true");
        bot.put("type", "bot");
        bot.put("subtype", botName);
        bot.put("player", String.valueOf(client.getID()));
        client.sendTCP(bot);
        System.out.println("Asked for bot " + botName);
    }

    /**
     * This method asks for all bots
     */
    public static void askForBots(Client client) {
        Map<String, String> bot = new HashMap<>();
        bot.put("game", "true");
        bot.put("type", "bot");
        bot.put("subtype", "askForBots");
        bot.put("player", String.valueOf(client.getID()));
        client.sendTCP(bot);
    }

    /**
     * This method damages the bot
     * @param damage The damage to deal
     */
    public static void damageBot(int damage) {
        Map<String, String> bot = new HashMap<>();
        bot.put("game", "true");
        bot.put("type", "bot");
        bot.put("subtype", "damageBot");
        bot.put("damage", String.valueOf(damage));
        bot.put("player", String.valueOf(client.getID()));
        client.sendTCP(bot);
    }

    /**
     * This method sends the destruction packet
     */
    public static void sendDestructionPacket() {
        Map<String, String> bot = new HashMap<>();
        bot.put("game", "true");
        bot.put("type", "bot");
        bot.put("subtype", "botClear");
        bot.put("player", String.valueOf(client.getID()));
        client.sendTCP(bot);
    }



}
