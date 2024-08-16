package ee.taltech.crossovertwo.game.HUD.messages;

import com.badlogic.gdx.Gdx;
import ee.taltech.crossovertwo.game.HUD.hud.HUD;

public abstract class Message {
    private static final float  DURATION = 5f; // in seconds

    private static String text;
    private static float timer;

    /**
     * This method shows a message on the screen
     * @param text The text to show
     * @param type The type of the message
     */
    public static void showMessage(String text, MsgType type) {
        Message.text = text;
        HUD.setMessageColor(type.getColor());
        timer = DURATION;
    }

    /**
     * This method shows a message on the screen
     * @param text The text to show
     */
    public static void showMessage(String text) {
        showMessage(text, MsgType.INFO);
    }

    /**
     * This method updates the message
     */
    public static void update() {
        if (timer > 0) {
            timer -= Gdx.graphics.getDeltaTime();
        }
    }

    /**
     * This method checks if the message is active
     * @return True if the message is active, false otherwise
     */
    public static boolean isActive() {
        return timer > 0;
    }

    /**
     * This method renders the message
     */
    public static void render() {
        update();
        if (isActive()) {
            HUD.setMessage(text);
        } else {
            HUD.setMessage("");
        }
    }
}
