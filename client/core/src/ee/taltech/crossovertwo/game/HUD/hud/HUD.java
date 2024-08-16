package ee.taltech.crossovertwo.game.HUD.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import ee.taltech.crossovertwo.game.GameScreen;
import ee.taltech.crossovertwo.game.HUD.messages.Message;
import ee.taltech.crossovertwo.game.players.Player;
import ee.taltech.crossovertwo.utilities.GraphicVariables;

public abstract class HUD {

    private static Stage stage;
    private static Skin skin;
    private static Label healthLabel;
    private static Label timeLabel;
    private static Label nicknameLabel;
    private static Label messageLabel;
    private static Table innerTableForMessages;
    private static Label waveLabel;
    public static int wave = 0;

    /**
     * Initialize HUD.
     */
    public static void initialize() {
        skin = GraphicVariables.getSkin();

        stage = new Stage(GameScreen.hudPort);
        Gdx.input.setInputProcessor(stage);

        Label.LabelStyle labelStyle = GraphicVariables.getLabelStyle();

        // Create labels
        healthLabel = new Label("Health: 300", labelStyle);
        timeLabel = new Label("Time: 00:00", labelStyle);
        nicknameLabel = new Label("Nickname", labelStyle);
        messageLabel = new Label("", labelStyle);
        waveLabel = new Label("Wave: 0/30", labelStyle);

        // Create top table with background and line
        Table topTable = new Table();
        topTable.top().padTop(10);
        topTable.setFillParent(true);

        // Create inner table for labels
        Table innerTable = new Table();
        innerTable.center();
        innerTable.defaults().expandX().pad(5);
        innerTable.setBackground(GraphicVariables.getGeneralBackground());

        innerTableForMessages = new Table();
        innerTableForMessages.center();
        innerTableForMessages.defaults().expandX();
        innerTableForMessages.setBackground(GraphicVariables.getGeneralBackground());
        innerTableForMessages.add(messageLabel).center();

        // Add labels to inner table, adjust positioning as desired
        innerTable.add(healthLabel);
        innerTable.add(timeLabel);
        innerTable.add(waveLabel);
        innerTable.add(nicknameLabel);

        // Add inner table to top table
        topTable.add(innerTable).expandX().fillX().row();
        topTable.add(innerTableForMessages).expandX().fillX();

        // Add top table to the stage
        stage.addActor(topTable);
    }

    /**
     * Update health label.
     * @param hp new health value
     */
    public static void updateHealth(int hp) {
        healthLabel.setText("Health: " + hp);
    }

    /**
     * Update time label.
     * @param time new time value
     */
    public static void updateTime(String time) {
        timeLabel.setText("Time: " + time);
    }

    /**
     * Set nickname to HUD.
     * @param nickname nickname to set
     */
    public static void setNickname(String nickname) {
        nicknameLabel.setText(nickname);
    }

    /**
     * Update wave label.
     */
    public static void updateWave() {
        waveLabel.setText("Wave: " + wave + "/30");
    }

    /**
     * Set message to HUD.
     * @param message message to set
     */
    public static void setMessage(String message) {
        if (message.isEmpty()) {
            innerTableForMessages.setVisible(false);
            return;
        }
        innerTableForMessages.setVisible(true);
        messageLabel.setText(message);
    }

    /**
     * Set message color.
     * @param color color to set
     */
    public static void setMessageColor(Color color) {
        messageLabel.setColor(color);
    }

    /**
     * Render HUD with some info that belongs to HUD.
     */
    public static void render() {
        Message.render();
        Player.inventory.draw();
        GameScreen.hudPort.apply();
        stage.act();
        stage.draw();
        GameScreen.gamePort.apply();
    }

    /**
     * Dispose of the HUD.
     */
    public static void dispose() {
        stage.dispose();
        skin.dispose();
    }

    /**
     * Get stage.
     * @return stage
     */
    public static Stage getStage() {
        return stage;
    }
}
