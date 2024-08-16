package ee.taltech.crossovertwo.game.resources;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import ee.taltech.crossovertwo.game.players.Player;
import ee.taltech.crossovertwo.packets.PacketResources;
import ee.taltech.crossovertwo.utilities.GraphicVariables;
import ee.taltech.crossovertwo.utilities.NetworkVariables;

import java.util.ArrayList;
import java.util.List;

public class ResourceGenerator {

    private static List<ResourceGenerator> generators;
    public static void setGenerators(List<ResourceGenerator> generators) {
        ResourceGenerator.generators = generators;
    }

    private static int timer = 0;
    private int xCoord;
    private int yCoord;
    private int radius;
    private String textureName;
    private String resourceToSpawnName;
    private int timerForResources = 60 * 15; // no clue about time unit lmao, but this should be every N seconds
    private List<Resource> resourceList = new ArrayList<>();
    private static SpriteBatch batch = new SpriteBatch();
    private Texture texture;

    /**
     * Constructor to initialize the ResourceGenerator
     * @param xCoord The x coordinate of the generator
     * @param yCoord The y coordinate of the generator
     * @param radius The radius of the generator
     * @param textureName The texture name of the generator
     * @param generatedResource The resource to spawn
     */
    public ResourceGenerator(int xCoord, int yCoord, int radius, String textureName, String generatedResource) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.radius = radius;
        this.textureName = textureName; // ignore this
        this.resourceToSpawnName = generatedResource;
        this.texture = GraphicVariables.getGeneratorTexture(generatedResource);
    }

    /**
     * This method increments the timer
     */
    private static void incrementTimer() {
        timer++;
    }

    /**
     * This method spawns a resource
     */
    private void spawnResource() {
        if (timer % timerForResources == 0) {
            Resource generatedResource = new Resource(resourceToSpawnName);
            resourceList.add(generatedResource);
        }
    }

    /**
     * This method collects the resources
     * @return The list of resources to collect
     */
    public List<Resource> collectResources() {
        List<Resource> resourcesToCollect = new ArrayList<>(resourceList);
        resourceList.clear();
        return resourcesToCollect;
    }

    /**
     * This method renders the resource generators
     * @param camera The camera to render the generators on
     * @param player The player to collect the resources
     */
    public static void render(OrthographicCamera camera, Player player) {
        ResourceGenerator.incrementTimer();
        if (generators != null) {
            List<ResourceGenerator> generatorsToRender = new ArrayList<>(generators);
            for (ResourceGenerator generator : generatorsToRender) {

                // rendering generator
                batch.begin();
                batch.setProjectionMatrix(camera.combined);
                batch.draw(generator.texture, generator.xCoord - generator.radius, generator.yCoord - generator.radius, generator.radius * 2, generator.radius * 2);
                batch.end();

                // collecting resources
                generator.spawnResource();
                Circle generatorArea = new Circle(generator.xCoord, generator.yCoord, generator.radius);
                if (generatorArea.contains(player.getxCoord(), player.getyCoord()) && !generator.resourceList.isEmpty()) {
                    player.addResource(generator.collectResources());
                }
            }
            generatorsToRender.clear();
            generatorsToRender = null;
        } else {
            PacketResources.askForGenerators(NetworkVariables.getClient());
        }
    }
}
