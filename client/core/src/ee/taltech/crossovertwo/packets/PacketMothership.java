package ee.taltech.crossovertwo.packets;

import java.util.HashMap;
import java.util.Map;

public class PacketMothership extends Packet {

    /**
     * This method sends the mothership hp
     * @param hp The hp of the mothership
     */
    public static void sendMothershipHp(int hp) {
        Map<String, String> mothershipInfo = new HashMap<>();
        mothershipInfo.put("game", "true");
        mothershipInfo.put("type", "mothershiphp");
        mothershipInfo.put("mothershiphp", String.valueOf(hp));
        client.sendUDP(mothershipInfo);
    }

    /**
     * This method sends the mothership healed hp
     * @param hp The hp to heal
     */
    public static void sendMothershipHeal(int hp) {
        Map<String, String> mothershipInfo = new HashMap<>();
        mothershipInfo.put("game", "true");
        mothershipInfo.put("type", "mothershipheal");
        mothershipInfo.put("mothershipheal", String.valueOf(hp));
        client.sendUDP(mothershipInfo);
    }

}
