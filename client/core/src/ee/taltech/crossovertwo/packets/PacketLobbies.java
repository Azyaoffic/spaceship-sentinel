package ee.taltech.crossovertwo.packets;

import java.util.HashMap;
import java.util.Map;

public class PacketLobbies extends Packet {

    /**
     * Method to send the "get room" packet
     */
    public static void sendGetRoom() {
        System.out.println("Sending get room packjet!!!!!");
        Map<String, String> clientinfo = new HashMap<>();
        clientinfo.put("type", "lobbyGetRooms");
        client.sendTCP(clientinfo);
    }

    /**
     * Method to send the "create room" packet
     * @param lobbyName The name of the lobby
     */
    public static void sendCreateRoom(String lobbyName) {
        Map<String, String> clientinfo = new HashMap<>();
        clientinfo.put("type", "lobbyCreateRoom");
        clientinfo.put("name", lobbyName);
        client.sendTCP(clientinfo);
    }

    /**
     * Method to send the "connect to room" packet
     * @param lobbyName The name of the lobby
     */
    public static void sendConnectLobbyTo(String lobbyName) {
        Map<String, String> clientinfo = new HashMap<>();
        clientinfo.put("type", "lobbyConnectTo");
        clientinfo.put("name", lobbyName);
        client.sendTCP(clientinfo);
    }

    /**
     * Method to send the nickname of the player
     */
    public static void sendNickname(String nickname, String lobbyName) {
        Map<String, String> nicknameMap = new HashMap<>();
        nicknameMap.put("type", "nicknameAdd");
        nicknameMap.put("name", lobbyName);
        nicknameMap.put("nickname", nickname);
        client.sendTCP(nicknameMap);
    }

    /**
     * Method to send the "start game" packet
     * @param lobbyName The name of the lobby
     */
    public static void sendStartGame(String lobbyName) {
        Map<String, String> clientinfo = new HashMap<>();
        clientinfo.put("type", "lobbyStartGame");
        clientinfo.put("lobby", lobbyName);
        client.sendTCP(clientinfo);
    }
}
