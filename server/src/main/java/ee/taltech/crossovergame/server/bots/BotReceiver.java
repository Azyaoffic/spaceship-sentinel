package ee.taltech.crossovergame.server.bots;

import ee.taltech.crossovergame.server.GameServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotReceiver {

    private final GameServer gameServer;

    /**
     * Constructor for the BotReceiver
     * @param gameServer The game server
     */
    public BotReceiver(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    /**
     * Listener for the bot receiver
     * @param gameServer The game server
     * @param object The object to listen to
     */
    public void listener(GameServer gameServer, Object object) {
        Map<String, String> objectAsMap = (Map<String, String>) object;
        String subtype = objectAsMap.get("subtype");

        switch (subtype) {
            case "Tanky Bot":
                int client = Integer.parseInt(objectAsMap.get("player"));
                spawnBot(gameServer, 400, 25, 3, client);
                break;
            case "Damaging Bot":
                int client2 = Integer.parseInt(objectAsMap.get("player"));
                spawnBot(gameServer, 200, 50, 5, client2);
                break;
            case "Weakling":
                int client3 = Integer.parseInt(objectAsMap.get("player"));
                spawnBot(gameServer, 200, 25, 4, client3);
                break;
            case "askForBots":
                break;
            case "botClear":
                gameServer.botPlayers.clear();
                break;
            case "damageBot":
                int damage = Integer.parseInt(objectAsMap.get("damage"));
                for (BotPlayer botPlayer : new ArrayList<>(gameServer.botPlayers)) {
                    botPlayer.health -= damage;
                    System.out.println("Bot health: " + botPlayer.health);
                    if (botPlayer.health <= 0) {
                        gameServer.botPlayers.remove(botPlayer);
                    }
                }
                break;
        }
        moveAllBots();
        sendBots();
    }

    /**
     * Spawns a bot
     * @param gameServer The game server
     * @param health The health of the bot
     * @param damage The damage of the bot
     * @param speed The speed of the bot
     * @param client The client id
     */
    void spawnBot(GameServer gameServer, int health, int damage, int speed, int client) {
        System.out.println("Spawning bot");
        int x = Integer.parseInt(gameServer.clientPositions.get(client).split(";;")[0]) + 16;
        int y = 3200 - Integer.parseInt(gameServer.clientPositions.get(client).split(";;")[1]) + 10;
        System.out.println("x: " + x + " y: " + y);
        BotPlayer botPlayer = new BotPlayer(gameServer, health, damage, speed, x, y);
        gameServer.botPlayers.add(botPlayer);
    }

    /**
     * Converts the bots to maps
     * @return The list of maps
     */
    public List<Map<String, String>> convertBotsToMaps() {
        List<Map<String, String>> botList = new ArrayList<>();
        for (BotPlayer botPlayer : gameServer.botPlayers) {
            Map<String, String> bot = new HashMap<>();
            bot.put("type", "bots");
            bot.put("x", String.valueOf(botPlayer.x));
            bot.put("y", String.valueOf(3200 - botPlayer.y));
            bot.put("health", String.valueOf(botPlayer.health));
            bot.put("damage", String.valueOf(botPlayer.damage));
            bot.put("speed", String.valueOf(botPlayer.speed));
            bot.put("id", String.valueOf(botPlayer.id));
            bot.put("targetEnemyId", String.valueOf(botPlayer.targetEnemyId));
            botList.add(bot);
        }
        return botList;
    }

    /**
     * Sends the bots
     */
    private void sendBots() {
        Map<Integer, Object> botPacket = new HashMap<>();
        Map<String, String> botInfo = new HashMap<>();
        botInfo.put("type", "botsList");
        botPacket.put(0, botInfo);
        botPacket.put(-5, convertBotsToMaps());

        gameServer.sendUDPToConnectedClients(botPacket);
    }

    /**
     * Moves all the bots
     */
    private void moveAllBots() {
        for (BotPlayer botPlayer : gameServer.botPlayers) {
            botPlayer.move();
        }
    }
}
