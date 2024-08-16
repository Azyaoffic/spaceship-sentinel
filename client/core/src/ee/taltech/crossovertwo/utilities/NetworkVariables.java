package ee.taltech.crossovertwo.utilities;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import ee.taltech.crossovertwo.LobbiesScreen;
import ee.taltech.crossovertwo.packets.PacketLobbies;
import ee.taltech.crossovertwo.packets.PacketTurrets;

import java.io.IOException;
import java.util.HashMap;

public class NetworkVariables {
    static String host = "localhost";
    static Integer tcpPort = 8080;
    static Integer udpPort = 8081;
    static String playerNicknameTemp;
    static Client client;

    /**
     * This method creates a connection to the server
     */
    public static void createConnection() {
        client = new Client(1000000, 1000000);
        client.start();

        Kryo kryo = client.getKryo();
        kryo.register(HashMap.class);
        kryo.register(java.util.ArrayList.class);

        try {
            client.connect(5000, host, tcpPort, udpPort);
        } catch (IOException e) {
            try {
                setTaltechVariables();
                replaceConnection();
            } catch (Exception e1) {
                System.out.println("Could not connect to server");
            }
        }
        LobbiesScreen.setClient(client);
        PacketLobbies.updateClient(client);
        PacketTurrets.updateClient(client);
    }

    /**
     * This method sets the default variables
     */
    public static void setDefaultVariables() {
        host = "localhost";
        tcpPort = 8080;
        udpPort = 8081;
    }

    /**
     * This method sets the TalTech variables
     */
    public static void setTaltechVariables() {
        host = "193.40.255.11";
        tcpPort = 8080;
        udpPort = 8081;
    }

    /**
     * This method replaces the connection
     */
    public static void replaceConnection() {
        client.stop();
        createConnection();
    }

    /**
     * This method gets the player nickname
     * @return The player nickname
     */
    public static Client getClient() {
        return client;
    }

    /**
     * This method gets the player nickname
     * @return The player nickname
     */
    public static String getHost() {
        return host;
    }

    /**
     * This method sets the player nickname
     * @param playerNicknameTemp The player nickname
     */
    public static void setPlayerNicknameTemp(String playerNicknameTemp) {
        NetworkVariables.playerNicknameTemp = playerNicknameTemp;
    }
}
