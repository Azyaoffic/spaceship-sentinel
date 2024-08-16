package ee.taltech.crossovertwo.packets;

import ee.taltech.crossovertwo.game.items.weapon.ItemWeapon;

import java.util.HashMap;
import java.util.Map;

public class PacketPlayerWeapon extends Packet {

    /**
     * Sends the player weapon to the server
     * @param weapon The weapon of the player
     */
    public static void sendPlayerWeapon(ItemWeapon weapon) {
        Map<String, String> playerWeapon = new HashMap<>();
        playerWeapon.put("game", "true");
        playerWeapon.put("type", "weapon");
        if (weapon == null) {
            playerWeapon.put("weapon", "null");
        } else {
            playerWeapon.put("weapon", weapon.getName());
        }
        client.sendUDP(playerWeapon);
    }
}
