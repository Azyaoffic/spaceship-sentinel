package ee.taltech.crossovergame.server.resources;

import ee.taltech.crossovergame.server.GameServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourcePackets {

    /**
     * Send the resource generators to the clients
     * @param gameServer The game server
     * @param resourceHandler The resource handler
     */
    public static void sendGenerators(GameServer gameServer, ResourceHandler resourceHandler) {
        List<ResourceGenerators> generators = resourceHandler.getGenerators();

        Map<Integer, Object> generatorsPacket = new HashMap<>();
        Map<String, String> generatorInfo = new HashMap<>();
        generatorInfo.put("type", "generatorsList");
        generatorsPacket.put(0, generatorInfo);

        List<Map<String, String>> generatorsMaps = new ArrayList<>();
        for (ResourceGenerators generator : generators) {
            Map<String, String> generatorAsMap = new HashMap<>();
            generatorAsMap.put("xCoord", String.valueOf(generator.getxCoord()));
            generatorAsMap.put("yCoord", String.valueOf(generator.getyCoord()));
            generatorAsMap.put("radius", String.valueOf(generator.getRadius()));
            generatorAsMap.put("textureName", generator.getTextureName());
            generatorAsMap.put("resourceToSpawnName", generator.getResourceToSpawnName());

            generatorsMaps.add(generatorAsMap);
        }

        generatorsPacket.put(-5, generatorsMaps);

        gameServer.sendTCPToConnectedClients(generatorsPacket);
    }

}
