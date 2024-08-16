package ee.taltech.crossovertwo.packets;

import com.esotericsoftware.kryonet.Client;

import java.util.HashMap;
import java.util.Map;

public class PacketTurrets extends Packet {

    /**
     * This method creates a basic turret
     */
    public static void createBasicTurret() {
        Map<String, String> turret1 = new HashMap<>();
        turret1.put("game", "true");
        turret1.put("type", "turret");
        turret1.put("subtype", "createBasicTurret");
        turret1.put("player", String.valueOf(client.getID()));
        client.sendTCP(turret1);
    }

    /**
     * This method creates a plasm turret
     */
    public static void createPlasmTurret() {
        Map<String, String> turret2 = new HashMap<>();
        turret2.put("game", "true");
        turret2.put("type", "turret");
        turret2.put("subtype", "createPlasmTurret");
        turret2.put("player", String.valueOf(client.getID()));
        client.sendTCP(turret2);
    }

    /**
     * This method asks for turrets
     */
    public static void askForTurrets(Client client) {
        Map<String, String> bot = new HashMap<>();
        bot.put("game", "true");
        bot.put("type", "turret");
        bot.put("subtype", "askForTurrets");
        bot.put("player", String.valueOf(client.getID()));
        client.sendTCP(bot);
    }

    /**
     * This method sends the destruction packet
     * @param turretId The id of the turret
     */
    public static void sendDestructionPacket(int turretId) {
        Map<String, String> bot = new HashMap<>();
        bot.put("game", "true");
        bot.put("type", "turret");
        bot.put("subtype", "turretClear");
        bot.put("id", String.valueOf(turretId));
        client.sendTCP(bot);
    }

    /**
     * This method damages the turret
     * @param turretId The id of the turret
     * @param damage The damage to deal
     */
    public static void damageTurret(int turretId, int damage) {
        Map<String, String> turret = new HashMap<>();
        turret.put("game", "true");
        turret.put("type", "turret");
        turret.put("subtype", "damageTurret");
        turret.put("id", String.valueOf(turretId));
        turret.put("damage", String.valueOf(damage));
        client.sendTCP(turret);
    }
}
