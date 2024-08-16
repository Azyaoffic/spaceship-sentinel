package ee.taltech.crossovertwo.game.HUD.ingamemenu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import ee.taltech.crossovertwo.game.HUD.hud.HUD;
import ee.taltech.crossovertwo.utilities.GraphicVariables;
import ee.taltech.crossovertwo.utilities.ScreenManager;

public class InGameMenu {

    private static Stage stage;
    private static Skin skin;
    private static Label nameLabel;
    private static TextButton exitToMenuButton, exitGameButton;
    private static Table topTable;
    private static boolean isMenuVisible = false;

    /**
     * Initialize menu.
     */
    public static void initialize() {
        skin = GraphicVariables.getSkin();

        stage = HUD.getStage();

        Label.LabelStyle labelStyle = GraphicVariables.getLabelStyleForLabels();

        isMenuVisible = false;

        // Create labels
        nameLabel = new Label("Menu", labelStyle);
        exitToMenuButton = new TextButton("Exit to menu", skin);
        exitGameButton = new TextButton("Exit game", skin);

        // Create top table with background and line
        topTable = new Table();
        topTable.pad(200);
        // topTable.setDebug(true);
        topTable.setFillParent(true);

        // Create inner table for labels
        Table innerTable = new Table();
        innerTable.center().pad(10);
        innerTable.defaults().expand().center().fill().pad(5);
        innerTable.setBackground(GraphicVariables.getGeneralBackground());
        innerTable.add(nameLabel);
        innerTable.row();
        innerTable.add(exitToMenuButton).height(50);
        innerTable.row();
        innerTable.add(exitGameButton).height(50);

        // Add top table to the stage
        topTable.add(innerTable).expandX().fill();
        stage.addActor(topTable);

        topTable.setVisible(isMenuVisible);

        // Set button listeners
        exitToMenuButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                ScreenManager.reset();
                return true;
            }
        });

        exitGameButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                ScreenManager.exitGame();
                return true;
            }
        });
    }

    /**
     * Method to show the menu
     */
    public static void show() {
        isMenuVisible = !isMenuVisible;
        topTable.setVisible(isMenuVisible);
    }

    /**
     * Method to show the menu
     * @param isMenuVisible boolean
     */
    public static void show(boolean isMenuVisible) {
        InGameMenu.isMenuVisible = isMenuVisible;
        topTable.setVisible(isMenuVisible);
    }

    /**
     * Return True if the menu is visible.
     * @return boolean
     */
    public static boolean isMenuVisible() {
        return isMenuVisible;
    }
}
