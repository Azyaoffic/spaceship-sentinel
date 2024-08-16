package ee.taltech.crossovergame.server.resources;

import ee.taltech.crossovergame.server.GameServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ResourceHandler {

    private final List<ResourceGenerators> generators = new ArrayList<>();
    private final GameServer gameServer;
    private static final Random random = new Random();

    /**
     * Constructor for the ResourceHandler
     * @param gameserver The game server
     * @param amountOfGenerators The amount of generators to create
     */
    public ResourceHandler(GameServer gameserver, int amountOfGenerators) {
        this.gameServer = gameserver;

        for (int i = 0; i < amountOfGenerators; i++) {
            List<String> location = chooseLocation();
            generators.add(new ResourceGenerators(Integer.parseInt(location.get(0)), Integer.parseInt(location.get(1)), Integer.parseInt(location.get(2)),
                    "texture", location.get(3)));
        }
    }

    /**
     * Chooses a location for the generator
     * @return The location
     */
    private static List<String> chooseLocation() {
        // reading file
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("generatorscoordinates.csv")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // using rng to select one of them
        List<String> selectedLocation = records.get(random.nextInt(1,records.size()));
        return selectedLocation;
    }

    /**
     * Getter for the generators
     * @return The list of generators
     */
    public List<ResourceGenerators> getGenerators() {
        return generators;
    }
}
