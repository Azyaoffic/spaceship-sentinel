package ee.taltech.crossovertwo.game.HUD.messages;

import com.badlogic.gdx.graphics.Color;

public enum MsgType {
        INFO(Color.WHITE),
        WARNING(Color.YELLOW),
        ERROR(Color.RED);

    private final Color color;

    /**
     * Constructor for the message type
     * @param color The color of the message
     */
    MsgType(Color color) {
        this.color = color;
    }

    /**
     * This method returns the color of the message
     * @return The color of the message
     */
    public Color getColor() {
        return color;
    }
}
