package ee.taltech.crossovertwo.game.items.turret;

import com.badlogic.gdx.graphics.Texture;
import ee.taltech.crossovertwo.game.items.ItemToCraft;
import ee.taltech.crossovertwo.game.players.Player;
import ee.taltech.crossovertwo.packets.PacketTurrets;

public class ItemTurret extends ItemToCraft {
    private final int turretHealth;
    private final int turretDamage;
    private final int turretRange;
    private final int turretFireRate;
    private final Texture turretImg;

    /**
     * Constructor to initialize the ItemTurret
     * @param name The name of the turret
     * @param type The type of the turret
     * @param turretDamage The damage of the turret
     * @param turretRange The range of the turret
     * @param turretFireRate The fire rate of the turret
     * @param turretImg The image of the turret
     */
    public ItemTurret(String name, Type type, int coal, int iron, int gold,
                      int turretHealth, int turretDamage, int turretRange, int turretFireRate,
                      Texture turretImg) {
        super(name, type, coal, iron, gold);
        this.turretHealth = turretHealth;
        this.turretDamage = turretDamage;
        this.turretRange = turretRange;
        this.turretFireRate = turretFireRate;
        this.turretImg = turretImg;
    }

    /**
     * Constructor to initialize the WeaponSelection
     * @param turretSelection The TurretSelection to initialize the item
     */
    public ItemTurret(TurretSelection turretSelection) {
        this(turretSelection.getName(),
                turretSelection.getType(),
                turretSelection.getCoal(),
                turretSelection.getIron(),
                turretSelection.getGold(),
                turretSelection.getTurretHealth(),
                turretSelection.getTurretDamage(),
                turretSelection.getTurretRange(),
                turretSelection.getTurretFireRate(),
                new Texture(turretSelection.getTurretImg()));
        this.setDescription(turretSelection.getDescription());
    }

    @Override
    public void use() {
        if (this.getName().equals("Steelguard Sentry Turret")) {
            PacketTurrets.createBasicTurret();
        } else {
            PacketTurrets.createPlasmTurret();
        }
        Player.inventory.removeItem(this);
    }
}
