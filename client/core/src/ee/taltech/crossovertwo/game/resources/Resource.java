package ee.taltech.crossovertwo.game.resources;

public class Resource {
    private String name;

    /**
     * Constructor to initialize the Resource
     * @param name The name of the resource
     */
    public Resource(String name) {
        this.name = name;
    }

    /**
     * This method returns the name of the resource
     * @return The name of the resource
     */
    public String getName() {
        return name;
    }
}
