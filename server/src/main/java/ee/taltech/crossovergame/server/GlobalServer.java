package ee.taltech.crossovergame.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalServer {


    private final Server server;

    private final List<Lobby> lobbyRooms = new ArrayList<>();
    private final List<String> lobbyRoomsNames = new ArrayList<>();
    private final List<GameServer> gameServers = new ArrayList<>(); //servers that currently have a game running

    /**
     * Constructor for the GlobalServer
     */
    public GlobalServer() {
        this.server = new Server(1000000,1000000);
        Kryo kryo = server.getKryo();
        kryo.register(HashMap.class);
        kryo.register(java.util.ArrayList.class);

        try {
            server.start();
            server.bind(8080, 8081);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        server.addListener(new Listener() {
            public void received(Connection connection, Object object) {

                if (object instanceof HashMap) {
                    if (((HashMap<?, ?>) object).get("game") != null) {
                        GameServer serverWherePacketIsComingFrom = findServerWhereConnectionIs(connection);
                        serverWherePacketIsComingFrom.received(connection.getID(    ), object);
                    } else {
                        String type = (String) ((HashMap<?, ?>) object).get("type");
                        ((HashMap<?, ?>) object).remove("type");
                        switch (type) {
                            // lobby
                            case "lobbyGetRooms":
                                System.out.println("received a lobby packet!!!!11111");
                                sendLobbyNames(connection.getID());
                                break;
                            case "lobbyCreateRoom":
                                System.out.println("creating new room...");
                                String lobbyName = (String) ((HashMap<?, ?>) object).get("name");
                                lobbyRooms.add(new Lobby(connection.getID(), lobbyName));
                                lobbyRoomsNames.add(lobbyName);
                                sendLobbyNames(connection.getID());
                                break;
                            case "lobbyConnectTo":
                                System.out.println("connecting user to room");
                                String lobbyConnectToName = (String) ((HashMap<?, ?>) object).get("name");
                                Lobby lobbyToConnect = getLobby(lobbyConnectToName);
                                lobbyToConnect.addConnection(connection.getID());
                                lobbyToConnect.putNickname(connection.getID(), "Player" + connection.getID());
                                sendLobbyInfo(connection.getID(), lobbyConnectToName);
                                break;
                            case "lobbyStartGame":
                                String lobbyToStartGameIn = (String) ((HashMap<?, ?>) object).get("lobby");
                                Lobby lobbyWhereToStart = getLobby(lobbyToStartGameIn);
                                List<String> connectionsStr = lobbyWhereToStart.getConnections();
                                List<Integer> connectionsInt = convertListStringToListInteger(connectionsStr);
                                gameServers.add(new GameServer(server, connectionsInt));
                                startGameForConnectedClients();

                                sendGameStartMessage(lobbyWhereToStart);
                                break;
                            case "nicknameAdd":
                                System.out.println(object);
                                String nickname = (String) ((HashMap<?, ?>) object).get("nickname");
                                String lobbyNicksName = (String) ((HashMap<?, ?>) object).get("name");
                                Lobby lobbyToUpdateNicks = getLobby(lobbyNicksName);
                                lobbyToUpdateNicks.addConnection(connection.getID());
                                lobbyToUpdateNicks.putNickname(connection.getID(), nickname);
                                sendLobbyInfo(connection.getID(), lobbyNicksName);
                                break;
                        }
                    }
                }
            }

            @Override
            public void disconnected(Connection connection) {
                // remove empty lobby rooms
                for (Lobby lobby : new ArrayList<>(lobbyRooms)) {
                    if (lobby.getConnections().contains(String.valueOf(connection.getID()))) {
                        lobby.getConnections().remove(String.valueOf(connection.getID()));
                        lobby.getNicknames().remove(connection.getID());
                        if (lobby.getConnections().isEmpty()) {
                            lobbyRooms.remove(lobby);
                            lobbyRoomsNames.remove(lobby.getLobbyName());
                        }
                    }
                }
            }
        });

    }

    /**
     * Method to send the lobby names
     * @param getResponseId The response id
     */
    public void sendLobbyNames(int getResponseId) {
        Map<String, List<String>> lrn = new HashMap<>();
        lrn.put("type", new ArrayList<>(List.of("lobbyGetRooms")));
        lrn.put("info", lobbyRoomsNames);
        server.sendToTCP(getResponseId, lrn);
    }

    /**
     * Method to send the lobby info
     * @param connectToResponseId The response id
     * @param lobbyConnectToName The name of the lobby to connect to
     */
    public void sendLobbyInfo(int connectToResponseId, String lobbyConnectToName) {

        Lobby lobby = getLobby(lobbyConnectToName);
        if (lobby == null) {
            Map<String, List<String>> lrn = new HashMap<>();
            lrn.put("type", new ArrayList<>(List.of("connectionFail")));
            server.sendToTCP(connectToResponseId, lrn);
            return;
        }

        Map<String, List<String>> lrn = new HashMap<>();
        lrn.put("type", new ArrayList<>(List.of("LobbyPlayers")));
        lrn.put("info", lobby.parseNicknames());
        lrn.put("host", new ArrayList<>(List.of(String.valueOf(lobby.getHostId()))));

        sendToDedicatesClients(lobby, lrn);
    }

    /**
     * Method to get the lobby
     * @param lobbyName The name of the lobby
     * @return The lobby
     */
    public Lobby getLobby(String lobbyName) {
        for (Lobby lobby : lobbyRooms) {
            if (lobby.getLobbyName().equals(lobbyName)) {
                return lobby;
            }
        }
        return null;
    }

    /**
     * Method to send to dedicated clients
     * @param lobby The lobby
     * @param info The info
     */
    public void sendToDedicatesClients(Lobby lobby, Map<String, List<String>> info) {
        System.out.println(lobby.getConnections());
        for (String id : lobby.getConnections()) {
            // System.out.println(info + "  ::::::  " + id);
            server.sendToTCP(Integer.parseInt(id), info);
        }
    }

    /**
     * Method to start the game for connected clients
     */
    public void startGameForConnectedClients() {
        System.out.println("Will transfer code here later :clueless:");
    }

    /**
     * Method to convert list string to list integer
     * @param listToConvert The list to convert
     * @return The list integer
     */
    public List<Integer> convertListStringToListInteger(List<String> listToConvert) {
        List<Integer> result = listToConvert.stream()
                .map(Integer::valueOf)
                .toList();

        return result;
    }

    /**
     * Method to find server where connection is
     * @param connection The connection
     * @return The game server
     */
    public GameServer findServerWhereConnectionIs(Connection connection) {
        for (GameServer gameServer : gameServers) {
            if (gameServer.getClients().contains(connection.getID())) {
                return gameServer;
            }
        }
        throw new RuntimeException("Player seems to not be in any server but sends game packet??????");
    }

    /**
     * Method to send game start message
     * @param lobbyWhereToStart The lobby where to start
     */
    public void sendGameStartMessage(Lobby lobbyWhereToStart) {
        Map<String, List<String>> lrn = new HashMap<>();
        lrn.put("type", new ArrayList<>(List.of("LobbyClientStartGame")));
        sendToDedicatesClients(lobbyWhereToStart, lrn);
        lobbyRooms.remove(lobbyWhereToStart);
        lobbyRoomsNames.remove(lobbyWhereToStart.getLobbyName());
    }

    /**
     * Main method
     * @param args The arguments
     */
    public static void main(String[] args) {
        GlobalServer globalServer = new GlobalServer();
    }
}
