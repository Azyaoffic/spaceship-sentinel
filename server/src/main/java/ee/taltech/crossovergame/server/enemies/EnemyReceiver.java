package ee.taltech.crossovergame.server.enemies;

import ee.taltech.crossovergame.server.GameServer;

import java.util.*;

public class EnemyReceiver {
    private final Random randomSt = new Random();
    private final GameServer gameServer;

    /**
     * Constructor for the EnemyReceiver
     * @param gameServer The game server
     */
    public EnemyReceiver(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    /**
     * Listener for the enemy receiver
     * @param object The object to listen to
     */
    public void listener(Object object) {
        Map<String, String> objectAsMap = (Map<String, String>) object;
        String subtype = objectAsMap.get("subtype");

        switch (subtype) {
            case "createWave":
                spawnEnemyWave(Integer.parseInt(objectAsMap.get("amount")));
                break;
            case "damageEnemy":
                try {
                    Enemy foundEnemy = findEnemyById(Integer.parseInt(objectAsMap.get("id")));
                    foundEnemy.damageEnemy(Integer.parseInt(objectAsMap.get("damage")));
                } catch (Exception e) {
                    System.out.println("error lmao, it's likely that enemy is already dead " + e);
                    sendClearingPacket();
                }
                break;
            case "askForEnemies":
                break;
        }

        sendAllEnemies(gameServer);
    }

    /**
     * Find an enemy by its id
     * @param enemyId The id of the enemy
     * @return The enemy
     */
    private Enemy findEnemyById(int enemyId) {
        return gameServer.enemies.stream().filter(enemy -> enemy.netId == enemyId).findFirst().get();
    }

    /**
     * Convert an enemy to a map
     * @param x The x coordinate of the enemy
     * @param y The y coordinate of the enemy
     * @param width The width of the enemy
     * @param height The height of the enemy
     * @param texture The texture of the enemy
     * @param hp The health points of the enemy
     * @param netId The id of the enemy
     * @param targetX The x coordinate of the target
     * @param targetY The y coordinate of the target
     * @param damage The damage of the enemy
     * @return The enemy as a map
     */
    public Map<String, String> convertEnemyToMap(int x, int y, float rotation, int width, int height, String texture, int hp, int netId, int targetX, int targetY, int damage) {
        Map<String, String> enemy = new HashMap<>();
        enemy.put("type", "enemies");
        enemy.put("x", String.valueOf(x));
        enemy.put("y", String.valueOf(y));
        enemy.put("rotation", String.valueOf(rotation));
        enemy.put("width", String.valueOf(width));
        enemy.put("height", String.valueOf(height));
        enemy.put("texture", texture);
        enemy.put("hp", String.valueOf(hp));
        enemy.put("netId", String.valueOf(netId));
        enemy.put("targetX", String.valueOf(targetX));
        enemy.put("targetY", String.valueOf(targetY));
        enemy.put("damage", String.valueOf(damage));
        return enemy;
    }

    /**
     * Send an enemy to the clients
     * @param enemyList The list of enemies
     */
    public void sendEnemyList(List<Map<String, String>> enemyList) {
        Map<Integer, Object> enemiesPacket = new HashMap<>();
        Map<String, String> enemyInfo = new HashMap<>();
        enemyInfo.put("type", "enemiesList");
        enemiesPacket.put(0, enemyInfo);
        enemiesPacket.put(-5, enemyList);

        gameServer.sendTCPToConnectedClients(enemiesPacket);
    }

    /**
     * Send a clearing packet to the clients (clears the enemies list)
     */
    private void sendClearingPacket() {
        Map<Integer, Map<String, String>> enemiesPacket = new HashMap<>();
        Map<String, String> enemyInfo = new HashMap<>();
        enemyInfo.put("type", "enemiesClear");
        enemiesPacket.put(0, enemyInfo);

        gameServer.sendUDPToConnectedClients(enemiesPacket);
    }

    /**
     * Send all enemies to the clients
     * @param gameServer The game server
     */
    public void sendAllEnemies(GameServer gameServer) {
        List<Map<String, String>> enemiesToSend = new ArrayList<>();
        if (gameServer.enemies.isEmpty()) {
            sendClearingPacket();
            return;
        }
        for (Enemy enemy : gameServer.enemies) {
            enemy.moveEnemy();
            enemiesToSend.add(convertEnemyToMap(enemy.x, 3200 - enemy.y, enemy.rotation, enemy.width, enemy.height, enemy.textureFile, enemy.hp, enemy.netId, enemy.targetPlayerX, 3200 - enemy.targetPlayerY, enemy.damage));
        }
        sendEnemyList(enemiesToSend);
    }

    /**
     * Create an amount of enemies.
     * @param amount_of_enemies amount to add
     */
    public void spawnEnemyWave(int amount_of_enemies) {
        List<Enemy> enemiesToAdd = new ArrayList<>();
        for (int i = 0; i < amount_of_enemies; i++) {
            EnemyType enemyType = EnemyType.getRandomEnemyType();
            List<Integer> coords = generateInitialCoords();
            enemiesToAdd.add(new Enemy(coords.get(0), coords.get(1), 40, 40, enemyType.texture, enemyType.hp, gameServer, enemyType.damage, enemyType.speed));
        }
        gameServer.enemies.addAll(enemiesToAdd);
    }

    /**
     * Method to generate initial coordinates for the player.
     * Initial coordinates are generated randomly
     * until the player is not colliding with any world objects.
     */
    private List<Integer> generateInitialCoords() {
        int xCoord = randomSt.nextInt(0, 100);
        int yCoord = randomSt.nextInt(0, 100);
        while (gameServer.mapGrid[yCoord][xCoord] == 1) {
            xCoord = randomSt.nextInt(0, 100);
            yCoord = randomSt.nextInt(0, 100);
        }
        return List.of(xCoord * 32, yCoord * 32);
    }

}
