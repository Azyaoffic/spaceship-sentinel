package ee.taltech.crossovertwo.packets;

import ee.taltech.crossovertwo.game.players.Player;

import java.util.HashMap;
import java.util.Map;

public class PacketPlayersCoordinates extends Packet {

    /**
     * Method to send the full packet
     * @param xCoord The x coordinate of the player
     * @param yCoord The y coordinate of the player
     * @param angle The angle of the player
     */
    public static void sendFullPacket(int xCoord, int yCoord, float angle) {
        sendNickname();
        sendCoords(xCoord, yCoord, angle);
        PacketPlayerWeapon.sendPlayerWeapon(Player.getMainWeapon());
    }

    /**
     * Method to send the coordinates of the player
     * @param xCoord The x coordinate of the player
     * @param yCoord The y coordinate of the player
     */
    public static void sendCoords(int xCoord, int yCoord, float angle) {
        Map<String, String> coordinates = new HashMap<>();
        coordinates.put("game", "true");
        coordinates.put("type", "playercoords");
        coordinates.put("x", String.valueOf(xCoord));
        coordinates.put("y", String.valueOf(yCoord));
        coordinates.put("angle", String.valueOf(angle));
        client.sendUDP(coordinates);
    }

    /**
     * Method to send the nickname of the player
     */
    public static void sendNickname() {
        Map<String, String> nicknameMap = new HashMap<>();
        nicknameMap.put("game", "true");
        nicknameMap.put("type", "nickname");
        nicknameMap.put("nickname", Player.getNickname());
        client.sendTCP(nicknameMap);
    }

    /**
     * Method to send the change of status
     * @param status The status of the player
     */
    public static void sendChangeStatus(boolean status) {
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("game", "true");
        statusMap.put("type", "status");
        statusMap.put("status", String.valueOf(status));
        client.sendTCP(statusMap);
    }

}
