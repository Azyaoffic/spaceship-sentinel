package ee.taltech.crossovertwo.packets;

import com.esotericsoftware.kryonet.Client;
import ee.taltech.crossovertwo.utilities.NetworkVariables;

public abstract class Packet {

    protected static Client client = NetworkVariables.getClient();

    /**
     * This method updates the client
     */
    public static void updateClient(Client client) {
        Packet.client = client;
    }

}
