package ee.taltech.crossovergame.server.resources;

public class ResourceGenerators {
    private int timer = 0;
    private int xCoord;
    private int yCoord;
    private int radius;
    private String textureName;
    private String resourceToSpawnName;

    /**
     * Constructor for the ResourceGenerators
     * @param xCoord The x coordinate of the generator
     * @param yCoord The y coordinate of the generator
     * @param radius The radius of the generator
     * @param textureName The texture of the generator
     * @param generatedResource The resource to spawn
     */
    public ResourceGenerators(int xCoord, int yCoord, int radius, String textureName, String generatedResource) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.radius = radius;
        this.textureName = textureName;
        this.resourceToSpawnName = generatedResource;
    }

    /**
     * Get the x coordinate of the generator
     * @return The x coordinate of the generator
     */
    public int getxCoord() {
        return xCoord;
    }

    /**
     * Get the y coordinate of the generator
     * @return The y coordinate of the generator
     */
    public int getyCoord() {
        return yCoord;
    }

    /**
     * Get the radius of the generator
     * @return The radius of the generator
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Get the texture of the generator
     * @return The texture of the generator
     */
    public String getTextureName() {
        return textureName;
    }

    /**
     * Get the resource to spawn
     * @return The resource to spawn
     */
    public String getResourceToSpawnName() {
        return resourceToSpawnName;
    }

}
