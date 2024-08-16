package ee.taltech.crossovertwo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import ee.taltech.crossovertwo.runnables.SetScreenRunnable;
import ee.taltech.crossovertwo.utilities.GraphicVariables;
import ee.taltech.crossovertwo.utilities.MenuBackground;
import ee.taltech.crossovertwo.utilities.ScreenManager;

public class StatsScreen extends ScreenAdapter {

    private final crossovertwo game;
    private FitViewport viewport;
    private Skin skin;
    private Stage stage;

    /**
     * Constructor to initialize the StatsScreen
     * @param game The game instance
     */
    public StatsScreen(crossovertwo game) {
        this.game = game;
    }

    /**
     * Method to show the StatsScreen
     * This method initializes the UI elements and adds them to the stage
     * It also adds listeners to the buttons
     * This method is called when the screen is shown
     */
    @Override
    public void show() {
        viewport = new FitViewport(800, 800);
        skin = GraphicVariables.getSkin();
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setBackground(GraphicVariables.getGeneralBackground());
        mainTable.center().pad(20);
        mainTable.defaults().pad(5);
        stage.addActor(mainTable); // Add table to the stage

        Label titleLabel = new Label("Statistics", GraphicVariables.getLabelStyleForLabels());

        Button backButton = new TextButton("Back to menu", skin);

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.postRunnable(new SetScreenRunnable(ScreenManager.ScreenEnum.MENU));
            }
        });

        mainTable.add(titleLabel).row();
        mainTable.add(Score.getGameSummary()).growX().expand().fill().row();
        mainTable.add(backButton).height(40).width(300);
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
        MenuBackground.resize(width, height);
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

        MenuBackground.render();

        viewport.apply();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }
}
