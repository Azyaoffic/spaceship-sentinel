package ee.taltech.crossovertwo.packets;

import java.util.HashMap;
import java.util.Map;

public class PacketGameEnd extends Packet {

    /**
     * This method sends the game end packet
     */
    public static void sendGameEnd() {
        sendGameStatus(false);
    }

    /**
     * This method sends the game win packet
     */
    public static void sendGameWin() {
        sendGameStatus(true);
    }

    /**
     * This method sends the game status
     * @param win The status of the game
     */
    private static void sendGameStatus(boolean win) {
        Map<String, String> gameInfo = new HashMap<>();
        gameInfo.put("game", "true");
        gameInfo.put("type", "gameend");
        gameInfo.put("win", String.valueOf(win));
        client.sendTCP(gameInfo);
    }

}
