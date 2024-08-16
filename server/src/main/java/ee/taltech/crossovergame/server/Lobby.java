package ee.taltech.crossovergame.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lobby {
    private final List<String> connections = new ArrayList<>();
    private final Map<Integer, String> nicknames = new HashMap<>();
    private final String lobbyName;
    private int hostId;

    /**
     * Constructor for the Lobby
     * @param hostClientId The id of the host client
     * @param lobbyName The name of the lobby
     */
    public Lobby(int hostClientId, String lobbyName) {
        this.connections.add(String.valueOf(hostClientId));
        this.hostId = hostClientId;
        this.lobbyName = lobbyName;
        System.out.println(connections);
    }

    /**
     * Method to add a connection
     * @param clientId The id of the client
     */
    public void addConnection(int clientId) {
        if (!connections.contains(String.valueOf(clientId))) {
            connections.add(String.valueOf(clientId));
        }
    }

    /**
     * Method to put a nickname
     * @param clientId The id of the client
     * @param nickname The nickname of the client
     */
    public void putNickname(int clientId, String nickname) {
        nicknames.put(clientId, nickname);
    }

    /**
     * Method to parse the nicknames
     * @return The list of nicknames
     */
    public List<String> parseNicknames() {
        List<String> nicknames = new ArrayList<>();
        for (Map.Entry<Integer, String> nick : this.nicknames.entrySet()) {
            nicknames.add(String.valueOf(nick.getKey()));
            nicknames.add(nick.getValue());
        }
        return nicknames;
    }

    /**
     * Method to remove a connection
     * @param clientId The id of the client
     */
    public void removeConnection(int clientId) {
        connections.remove(clientId);
    }

    /**
     * Method to get the connections
     * @return The list of connections
     */
    public List<String> getConnections() {
        return connections;
    }

    /**
     * Method to get the lobby name
     * @return The name of the lobby
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * Method to get the host id
     * @return The id of the host
     */
    public int getHostId() {
        return hostId;
    }

    /**
     * Method to get the nicknames
     * @return The map of nicknames
     */
    public Map<Integer, String> getNicknames() {
        return nicknames;
    }
}
