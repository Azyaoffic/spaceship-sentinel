package ee.taltech.crossovertwo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ee.taltech.crossovertwo.Score;
import ee.taltech.crossovertwo.crossovertwo;
import ee.taltech.crossovertwo.utilities.GraphicVariables;
import ee.taltech.crossovertwo.utilities.ScreenManager;


public class WinScreen extends ScreenAdapter {
    private final crossovertwo game;
    private Stage stage;
    private TextButton backToMenuButton;
    private Viewport viewport;
    private Skin skin;

    /**
     * Constructor to initialize the WinScreen
     */
    public WinScreen(crossovertwo game) {
        this.game = game;
    }

    /**
     * Method to show the GameOverScreen
     * This method initializes the UI elements and adds them to the stage
     * It also adds listeners to the buttons
     * This method is called when the screen is shown
     */
    public void show() {
        viewport = new FitViewport(800, 800);
        skin = GraphicVariables.getSkin();

        stage = new Stage(viewport);

        Gdx.input.setInputProcessor(stage);
        // Initialize a table for UI layout
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table); // Add table to the stage

        // Initialize UI elements
        backToMenuButton = new TextButton("Back to the menu", skin);

        // Add listeners to buttons
        backToMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Print to the console when back to menu button is clicked
                System.out.println("Back to menu button clicked");
                // Go to the menu when back to menu button is clicked
                ScreenManager.reset();
            }
        });

        // Add UI elements to the table
        table.add(new Label("YOU'RE WINNER", GraphicVariables.getLabelStyleForLabels())).padBottom(50);
        table.row();
        table.add(Score.getLocalGameSummary());
        table.row();
        table.add(backToMenuButton).width(200).height(50).padBottom(10);
    }

    /**
     * Method to resize the screen
     * This method updates the viewport when the screen is resized
     * @param width The new width of the screen
     * @param height The new height of the screen
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    /**
     * Method to render the screen
     * This method clears the screen and updates/draws the stage
     * @param delta The time in seconds since the last render
     */
    @Override
    public void render(float delta) {
        // Clear the screen and update/draw the stage
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    /**
     * Method to hide the screen
     * This method clears the stage when the screen is hidden
     */
    @Override
    public void hide() {
        stage.clear();
    }

    /**
     * Method to dispose the screen
     * This method disposes the stage resources when the screen is disposed
     */
    @Override
    public void dispose() {
        try {
            stage.dispose();
        } catch (Exception e) {
            System.out.println("Error disposing stage - it was likely never shown.");
        }
    }
}
