package ee.taltech.crossovertwo.packets;

import com.esotericsoftware.kryonet.Client;

import java.util.HashMap;
import java.util.Map;

public class PacketResources extends Packet {

    /**
     * This method asks for generators
     * @param client The client to send the packet from
     */
    public static void askForGenerators(Client client) {
        Map<String, String> resources = new HashMap<>();
        resources.put("game", "true");
        resources.put("type", "generators");
        client.sendTCP(resources);
    }

}
