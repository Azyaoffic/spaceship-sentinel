package ee.taltech.crossovergame.server.turrets;

import ee.taltech.crossovergame.server.GameServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TurretReceiver {

    private final GameServer gameServer;

    /**
     * Constructor for the TurretReceiver
     * @param gameServer The game server
     */
    public TurretReceiver(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    /**
     * Listener for the turret receiver
     * @param gameServer The game server
     * @param object The object to listen to
     */
    public void listener(GameServer gameServer, Object object) {
        Map<String, String> objectAsMap = (Map<String, String>) object;
        String subtype = objectAsMap.get("subtype");

        switch (subtype) {
            case "createBasicTurret":
                int client1 = Integer.parseInt(objectAsMap.get("player"));
                spawnTurret1(gameServer, 200, 10, 300, 10, client1);
                break;
            case "createPlasmTurret":
                int client2 = Integer.parseInt(objectAsMap.get("player"));
                spawnTurret2(gameServer, 300, 100, 400, 100, client2);
                break;
            case "askForTurrets":
                break;
            case "damageTurret":
                try {
                    int turretId = Integer.parseInt(objectAsMap.get("id"));
                    int damage = Integer.parseInt(objectAsMap.get("damage"));
                    Turret foundTurret = gameServer.turrets.stream().filter(turret -> turret.id == turretId).findFirst().get();
                    foundTurret.damageTurret(damage);
                    if (foundTurret.health <= 0) {
                        sendClearingPacket();
                    }
                } catch (Exception e) {
                    sendClearingPacket();
                }
                break;
        }
        rotateAllTurrets();
        sendTurrets();
    }

    /**
     * Spawns a turret
     * @param gameServer The game server
     * @param health The health of the turret
     * @param damage The damage of the turret
     * @param range The range of the turret
     * @param fireRate The fire rate of the turret
     * @param client The client
     */
    void spawnTurret1(GameServer gameServer, int health, int damage, int range, int fireRate, int client) {
        int x = Integer.parseInt(gameServer.clientPositions.get(client).split(";;")[0]);
        int y = 3200 - Integer.parseInt(gameServer.clientPositions.get(client).split(";;")[1]);
        Turret turret = new Turret(gameServer, x, y, 0, health, damage, range, fireRate, "basicTurret.png");
        gameServer.turrets.add(turret);
    }

    /**
     * Spawns a second type turret
     * @param gameServer The game server
     * @param health The health of the turret
     * @param damage The damage of the turret
     * @param range The range of the turret
     * @param fireRate The fire rate of the turret
     * @param client The client
     */
    void spawnTurret2(GameServer gameServer, int health, int damage, int range, int fireRate, int client) {
        int x = Integer.parseInt(gameServer.clientPositions.get(client).split(";;")[0]);
        int y = 3200 - Integer.parseInt(gameServer.clientPositions.get(client).split(";;")[1]);
        Turret turret = new Turret(gameServer, x, y, 0, health, damage, range, fireRate, "plasmTurret.png");
        gameServer.turrets.add(turret);
    }

    /**
     * Convert turrets to maps
     * @return The list of turrets as maps
     */
    public List<Map<String, String>> convertTurretsToMaps() {
        List<Map<String, String>> turretList = new ArrayList<>();
        for (Turret turret : gameServer.turrets) {
            Map<String, String> turretMap = new HashMap<>();
            turretMap.put("type", "turrets");
            turretMap.put("x", String.valueOf(turret.x));
            turretMap.put("y", String.valueOf(3200 - turret.y));
            turretMap.put("angle", String.valueOf(turret.rotationAngle));
            turretMap.put("health", String.valueOf(turret.health));
            turretMap.put("damage", String.valueOf(turret.damage));
            turretMap.put("range", String.valueOf(turret.range));
            turretMap.put("fireRate", String.valueOf(turret.fireRate));
            turretMap.put("texture", turret.texture);
            turretMap.put("id", String.valueOf(turret.id));
            turretMap.put("targetEnemyId", String.valueOf(turret.targetEnemyId));
            turretList.add(turretMap);
        }
        return turretList;
    }

    /**
     * Send turrets to the clients
     */
    public void sendTurrets() {
        Map<Integer, Object> turretPacket = new HashMap<>();
        Map<String, String> turretInfo = new HashMap<>();
        turretInfo.put("type", "turretsList");
        turretPacket.put(0, turretInfo);
        turretPacket.put(-5, convertTurretsToMaps());

        gameServer.sendUDPToConnectedClients(turretPacket);
    }

    /**
     * Send a clearing packet to the clients (clears the turrets list)
     */
    private void sendClearingPacket() {
        Map<Integer, Map<String, String>> turretsPacket = new HashMap<>();
        Map<String, String> turretInfo = new HashMap<>();
        turretInfo.put("type", "turretsClear");
        turretsPacket.put(0, turretInfo);

        gameServer.sendUDPToConnectedClients(turretsPacket);
    }

    /**
     * Rotate all turrets
     */
    private void rotateAllTurrets() {
        for (Turret turret : gameServer.turrets) {
            turret.FindTargetEnemy();
            turret.rotate();
        }
    }
}
