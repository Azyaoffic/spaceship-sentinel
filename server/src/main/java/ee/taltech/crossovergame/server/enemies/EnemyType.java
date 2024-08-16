package ee.taltech.crossovergame.server.enemies;

public enum EnemyType {

    FLYINGYELLOW("enemy2.png", 100, 2, 25),
    FlYINGRED("enemy3.png", 50, 4, 40),
    GREEN("enemy1.png", 200, 1, 50),;

    public String texture;
    public int hp;
    public int speed;
    public int damage;

    /**
     * Constructor for the enemy type
     * @param texture The texture of the enemy
     * @param hp The health points of the enemy
     * @param speed The speed of the enemy
     * @param damage The damage of the enemy
     */
    EnemyType(String texture, int hp, int speed, int damage) {
        this.texture = texture;
        this.hp = hp;
        this.speed = speed;
        this.damage = damage;
    }

    /**
     * Get a random enemy type
     * @return A random enemy type
     */
    public static EnemyType getRandomEnemyType() {
        return values()[(int) (Math.random() * values().length)];
    }
}
