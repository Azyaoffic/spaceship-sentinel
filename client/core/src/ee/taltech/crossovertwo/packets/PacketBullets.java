package ee.taltech.crossovertwo.packets;

import ee.taltech.crossovertwo.game.items.weapon.BulletType;

import java.util.HashMap;
import java.util.Map;

public class PacketBullets extends Packet {

    /**
     * Method to send the bullets
     * @param xCoord The x coordinate of the bullet
     * @param yCoord The y coordinate of the bullet
     */
    public static void sendBullets(int xCoord, int yCoord, float angle, BulletType type) {
        Map<String, String> bullet = new HashMap<>();
        bullet.put("game", "true");
        bullet.put("type", "bullet");
        bullet.put("x", String.valueOf(xCoord));
        bullet.put("y", String.valueOf(yCoord));
        bullet.put("angle", String.valueOf(angle));
        bullet.put("bulletType", type.toString());
        client.sendUDP(bullet);
    }

}
