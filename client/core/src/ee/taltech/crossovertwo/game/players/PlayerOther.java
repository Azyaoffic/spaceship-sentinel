package ee.taltech.crossovertwo.game.players;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ee.taltech.crossovertwo.game.GameScreen;
import ee.taltech.crossovertwo.packets.PacketPlayerWeapon;
import ee.taltech.crossovertwo.utilities.GraphicVariables;

import java.util.HashMap;
import java.util.Map;

import static ee.taltech.crossovertwo.packets.PacketPlayersCoordinates.sendNickname;

public class PlayerOther {

    public static Map<Integer, Map<String, String>> receivedPlayers;
    public static Map<Integer, Map<String, String>> receivedPlayersStatus = new HashMap<>();
    private static Map<Integer, SpriteBatch> opponentBatches = new HashMap<>();
    private static Map<Integer, Map<String, String>> receivedNicknames = new HashMap<>();
    private static Map<Integer, Map<String, String>> receivedPlayerWeapons = new HashMap<>();
    public static Map<Integer, int[]> receivedPlayerCoordinates = new HashMap<>();
    private static final Texture pistol = new Texture("otherPistol.png");
    private static final Texture rifle = new Texture("otherRifle.png");
    private static final Texture laserPistol = new Texture("otherLaserPistol.png");
    private static final Texture laserGun = new Texture("otherLaserGun.png");
    private static final Texture otherImg = new Texture("other.png");

    /**
     * This method saves the received players
     * @param receivedPlayers The received players
     */
    public static void saveReceivedPlayers(Map<Integer, Map<String, String>> receivedPlayers) {
        PlayerOther.receivedPlayers = receivedPlayers;
    }

    /**
     * This method saves the received players status
     * @param receivedPlayersStatus The received players status
     */
    public static void saveReceivedPlayersStatus(Map<Integer, Map<String, String>> receivedPlayersStatus) {
        System.out.println(receivedPlayersStatus);
        PlayerOther.receivedPlayersStatus = receivedPlayersStatus;
    }

    /**
     * This method renders the received players
     * @param camera The camera to render the players on
     */
    public static void render(OrthographicCamera camera) {
        if (receivedPlayers == null) return;
        for (Map.Entry<Integer, Map<String, String>> entry : receivedPlayers.entrySet()) {
            int id = entry.getKey();
            if (id == GameScreen.clientId || id == 0) continue;
            if (!opponentBatches.containsKey(id)) {
                opponentBatches.put(id, new SpriteBatch());
            }

            if (!receivedPlayersStatus.containsKey(id)) {
                receivedPlayersStatus.put(id, Map.of("status", "true"));
            } else if (!Boolean.parseBoolean(receivedPlayersStatus.get(id).get("status"))) {
                continue;
            }

            SpriteBatch b = opponentBatches.get(id);
            String receivedNickname = null;

            try {
                receivedNickname = receivedNicknames.get(id).get("nickname");
            } catch (NullPointerException e) {
                sendNickname();
            }

            Texture receivedWeapon = null;
            try {
                receivedWeapon = switch (receivedPlayerWeapons.get(id).get("weapon")) {
                    case "M11 Compact" -> pistol;
                    case "AK74" -> rifle;
                    case "Mossberg 500" -> laserPistol;
                    case "NeuroBlade MK-X" -> laserGun;
                    default -> receivedWeapon;
                };
            } catch (NullPointerException e) {
                PacketPlayerWeapon.sendPlayerWeapon(Player.getMainWeapon());
            }

            int receivedXCoord = Integer.parseInt(entry.getValue().get("x"));
            int receivedYCoord = Integer.parseInt(entry.getValue().get("y"));
            float receivedRotation = Float.parseFloat(entry.getValue().get("angle"));
            b.begin();
            b.setProjectionMatrix(camera.combined);
            if (receivedNickname != null) {

                GraphicVariables.layout.setText(GraphicVariables.getGeneralFont(), receivedNickname);
                float textWidth = GraphicVariables.layout.width;

                GraphicVariables.getGeneralFont().draw(b, GraphicVariables.layout, receivedXCoord - textWidth / 2 + 32, receivedYCoord + Player.PLAYER_SIZE + 10);
            }
            if (receivedWeapon != null) {
                b.draw(receivedWeapon, receivedXCoord, receivedYCoord, 32, 16, 64, 64, 1, 1, receivedRotation, 0, 0, 64, 64, false, false);
            } else {
                b.draw(otherImg, receivedXCoord, receivedYCoord, 32, 16, otherImg.getWidth(), otherImg.getHeight(), 1, 1, receivedRotation, 0, 0, otherImg.getWidth(), otherImg.getHeight(), false, false);

            }
            b.end();
            PlayerOther.receivedPlayerCoordinates.put(id, new int[]{receivedXCoord, receivedYCoord});
        }
    }

    /**
     * Adds the received nicknames
     * @param nicknames The received nicknames
     */
    public static void addNickname(Map<Integer, Map<String, String>> nicknames) {
        PlayerOther.receivedNicknames = nicknames;
    }

    /**
     * Adds the received weapons
     * @param weapons The received weapons
     */
    public static void addWeapon(Map<Integer, Map<String, String>> weapons) {
        PlayerOther.receivedPlayerWeapons = weapons;
    }

    /**
     * Gets the received players
     * @return The received players
     */
    public static Map<Integer, Map<String, String>> getReceivedPlayersStatus() {
        return receivedPlayersStatus;
    }
}
