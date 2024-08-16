package ee.taltech.crossovergame.server;

import com.esotericsoftware.kryonet.Server;
import ee.taltech.crossovergame.server.astar.AStar;
import ee.taltech.crossovergame.server.astar.MapConverter;
import ee.taltech.crossovergame.server.bots.BotPlayer;
import ee.taltech.crossovergame.server.bots.BotReceiver;
import ee.taltech.crossovergame.server.enemies.Enemy;
import ee.taltech.crossovergame.server.enemies.EnemyReceiver;
import ee.taltech.crossovergame.server.resources.ResourceHandler;
import ee.taltech.crossovergame.server.resources.ResourcePackets;
import ee.taltech.crossovergame.server.turrets.Turret;
import ee.taltech.crossovergame.server.turrets.TurretReceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GameServer {

    private Server server;

    private List<Integer> clients;
    private final EnemyReceiver enemyReceiver = new EnemyReceiver(this);
    private final TurretReceiver turretReceiver = new TurretReceiver(this);
    private final BotReceiver botReceiver = new BotReceiver(this);
    private final Map<Integer, Map<String, String>> coordinates = new HashMap<>();
    private final Map<Integer, Map<String, String>> bullets = new HashMap<>();
    private final Map<Integer, Map<String, String>> nicknames = new HashMap<>();
    private final Map<Integer, Map<String, String>> statuses = new HashMap<>();
    private final Map<Integer, Map<String, String>> time = new HashMap<>();
    private final Map<Integer, Map<String, String>> playerWeapon = new HashMap<>();

    private long startingTime;
    private long endTime;
    private Map<Integer, Map<String, String>> mothershipHp = new HashMap<>();

    public List<Turret> turrets = new ArrayList<>();
    public List<Enemy> enemies = new ArrayList<>();
    public Map<Integer, String> clientPositions = new HashMap<>();

    private ResourceHandler resourceHandler;
    private int amountOfGenerators = 5;

    public int[][] mapGrid;
    public AStar aStar;

    public List<BotPlayer> botPlayers = new ArrayList<>();

    /**
     * Constructor for the GameServer
     * @param server The server object
     * @param connectedClients The list of connected clients
     */
    public GameServer(Server server, List<Integer> connectedClients) {
        Map<String, String> bulletInfo = new HashMap<>();
        bulletInfo.put("type", "bullet");
        bullets.put(0, bulletInfo);
        Map<String, String> coordsInfo = new HashMap<>();
        coordsInfo.put("type", "playercoords");
        coordinates.put(0, coordsInfo);
        Map<String, String> nicknameInfo = new HashMap<>();
        nicknameInfo.put("type", "nickname");
        nicknames.put(0, nicknameInfo);
        Map<String, String> timeInfo = new HashMap<>();
        timeInfo.put("type", "time");
        time.put(0, timeInfo);
        Map<String, String> mothershipInfo = new HashMap<>();
        mothershipInfo.put("type", "mothershiphp");
        mothershipInfo.put("mothershiphp", "10000");
        mothershipHp.put(0, mothershipInfo);
        Map<String, String> playerWeaponInfo = new HashMap<>();
        playerWeaponInfo.put("type", "weapon");
        playerWeapon.put(0, playerWeaponInfo);
        Map<String, String> statusInfo = new HashMap<>();
        statusInfo.put("type", "status");
        statuses.put(0, statusInfo);
        for (Integer player : connectedClients) {
            statuses.put(player, new HashMap<>(Map.of("status", "true")));
        }


        startingTime = System.currentTimeMillis();
        endTime = startingTime + 10 * 60 * 1000;  // 10 mins in milliseocnds

        this.server = server;
        this.clients = connectedClients;

        this.resourceHandler = new ResourceHandler(this, amountOfGenerators);

        this.mapGrid = MapConverter.readFile();
        this.aStar = new AStar(mapGrid);
    }

    /**
     * Method to receive packets from the clients
     * @param connection The connection object
     * @param object The object received
     */
    public void received(int connection, Object object){
        if (System.currentTimeMillis() >= endTime) {
            System.out.println("Time is up!");
            // server.stop();
        } else {
            Map<String, String> currentTime = new HashMap<>();
            currentTime.put("currentTime", String.valueOf(System.currentTimeMillis()));
            currentTime.put("endTime", String.valueOf(endTime));
            time.put(-10, currentTime);
            sendUDPToConnectedClients(time);
        }

        if (object instanceof HashMap) {
            String type = (String) ((HashMap<?, ?>) object).get("type");
            ((HashMap<?, ?>) object).remove("type");
            ((HashMap<?, ?>) object).remove("game");
            if (type.equals("mothershiphp")) {
                System.out.println("Received mothership hp");
                System.out.println(type);
            }
            switch (type) {
                // game
                case "playercoords":
                    coordinates.put(connection, (Map<String, String>) object);
                    sendUDPToConnectedClients(coordinates);

                    String clientCoords = ((Map<String, String>) object).get("x") + ";;" + ((Map<String, String>) object).get("y");
                    clientPositions.put(connection, clientCoords);

                    break;
                case "status":
                    statuses.put(connection, (Map<String, String>) object);
                    sendTCPToConnectedClients(statuses);
                    break;
                case "bullet":
                    bullets.put(connection, (Map<String, String>) object);
                    sendUDPToConnectedClients(bullets);
                    // System.out.println(bullets);
                    System.out.println("Opponent shot a bullet");
                    bullets.remove(connection);
                    break;
                case "nickname":
                    nicknames.put(connection, (Map<String, String>) object);
                    sendTCPToConnectedClients(nicknames);
                    break;
                case "enemies":
                    enemyReceiver.listener(object);
                    break;
                case "mothershiphp":
                    System.out.println(object);
                    int previousHp = Integer.parseInt(mothershipHp.get(0).get("mothershiphp"));
                    int receivedHp = Integer.parseInt(((Map<String, String>) object).get("mothershiphp"));
                    if (receivedHp < previousHp) {
                        ((HashMap<String, String>) object).put("type", "mothershiphp");
                        mothershipHp.put(0, (Map<String, String>) object);
                        sendTCPToConnectedClients(mothershipHp);
                    } else {
                        sendTCPToConnectedClients(mothershipHp);
                    }
                    break;
                case "healmothership":
                    int receivedHp2 = Integer.parseInt(((Map<String, String>) object).get("mothershiphp"));
                    ((HashMap<String, String>) object).put("mothershiphp", String.valueOf(receivedHp2));
                    ((HashMap<String, String>) object).put("type", "mothershiphp");
                    mothershipHp.put(0, (Map<String, String>) object);
                    sendTCPToConnectedClients(mothershipHp);
                    break;
                case "weapon":
                    playerWeapon.put(connection, (Map<String, String>) object);
                    sendUDPToConnectedClients(playerWeapon);
                    break;
                case "gameend":
                    ((HashMap<String, String>) object).put("type", "gameend");
                    Map<Integer, Map<String, String>> gameEnd = new HashMap<>();
                    gameEnd.put(0, (Map<String, String>) object);
                    sendTCPToConnectedClients(gameEnd);
                    break;
                case "generators":
                    ResourcePackets.sendGenerators(this, resourceHandler);
                    break;
                case "turret":
                    turretReceiver.listener(this, object);
                    break;
                case "bot":
                    botReceiver.listener(this, object);
                    break;
                default:
                    System.out.println("Unknown packet type");
                    System.out.println(object);
                    break;
            }
        }
    }

    /**
     * Method to send a TCP packet to all connected clients
     * @param data The data to send
     */
    public void sendTCPToConnectedClients(Object data) {
        for (Integer connection : clients) {
            server.sendToTCP(connection, data);
        }
    }

    /**
     * Method to send a UDP packet to all connected clients
     * @param data The data to send
     */
    public void sendUDPToConnectedClients(Object data) {
        for (Integer connection : clients) {
            server.sendToUDP(connection, data);
        }
    }

    /**
     * Get the list of connected clients
     * @return The list of connected clients
     */
    public List<Integer> getClients() {
        return clients;
    }

    /**
     * Get the map of statuses
     * @return The map of statuses
     */
    public Map<Integer, Map<String, String>> getStatuses() {
        return statuses;
    }
}
