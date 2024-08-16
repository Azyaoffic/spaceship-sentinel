package ee.taltech.crossovertwo.packets;

import com.esotericsoftware.kryonet.Client;

import java.util.HashMap;
import java.util.Map;

public class PacketEnemies extends Packet {

    /**
     * This method creates a wave of enemies
     * @param amount The amount of enemies to create
     */
    public static void createWave(Client client, int amount) {
        Map<String, String> enemy = new HashMap<>();
        enemy.put("game", "true");
        enemy.put("type", "enemies");
        enemy.put("subtype", "createWave");
        enemy.put("amount", String.valueOf(amount));
        client.sendTCP(enemy);
    }

    /**
     * This method damages the enemy
     * @param enemyId The id of the enemy
     * @param damage The damage to deal
     */
    public static void damageEnemy(Client client, int enemyId, int damage) {
        Map<String, String> enemy = new HashMap<>();
        enemy.put("game", "true");
        enemy.put("type", "enemies");
        enemy.put("subtype", "damageEnemy");
        enemy.put("id", String.valueOf(enemyId));
        enemy.put("damage", String.valueOf(damage));
        client.sendTCP(enemy);
    }

    /**
     * Ask for enemies
     * @param client The client to send the packet to
     */
    public static void askForEnemies(Client client) {
        Map<String, String> enemy = new HashMap<>();
        enemy.put("game", "true");
        enemy.put("type", "enemies");
        enemy.put("subtype", "askForEnemies");
        client.sendTCP(enemy);
    }

}
