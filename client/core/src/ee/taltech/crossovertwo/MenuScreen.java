package ee.taltech.crossovertwo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ee.taltech.crossovertwo.game.GameScreen;
import ee.taltech.crossovertwo.runnables.SetScreenRunnable;
import ee.taltech.crossovertwo.utilities.GraphicVariables;
import ee.taltech.crossovertwo.utilities.MenuBackground;
import ee.taltech.crossovertwo.utilities.NetworkVariables;
import ee.taltech.crossovertwo.utilities.ScreenManager;

public class MenuScreen extends ScreenAdapter {

    private final crossovertwo game;
    private Stage stage;
    private TextField hostField, tcpPortField, udpPortField, nicknameField;
    private TextButton startButton, connectButton, lobbiesButton, taltechButton, exitButton, settingButton;
    private Viewport viewport;
    private TextButton statsButton;

    /**
     * Constructor to initialize the MenuScreen
     * @param game The game instance
     */
    public MenuScreen(crossovertwo game) {
        this.game = game;
    }

    /**
     * Method to show the MenuScreen
     * This method initializes the UI elements and adds them to the stage
     * It also adds listeners to the buttons
     * This method is called when the screen is shown
     */
    @Override
    public void show() {
        viewport = new FitViewport(800, 800);
        Skin skin = GraphicVariables.getSkin();
        stage = new Stage(viewport);

        Gdx.input.setInputProcessor(stage);
        // Initialize a table for UI layout
        Table table = new Table();
        table.setFillParent(true);
        table.setBackground(GraphicVariables.getGeneralBackground());
        table.center().pad(100);
        table.defaults().height(40).pad(5);
        stage.addActor(table); // Add table to the stage

        // Initialize UI elements
        Label gameName = new Label("Spaceship Sentinel", GraphicVariables.getNameStyle());
        gameName.setAlignment(1);
        nicknameField = new TextField("Player", skin);
        hostField = new TextField("", skin);
        tcpPortField = new TextField("", skin);
        udpPortField = new TextField("", skin);
        startButton = new TextButton("Start game", skin);
        connectButton = new TextButton("Connect to server", skin);
        lobbiesButton = new TextButton("Connect to local server", skin);
        taltechButton = new TextButton("Connect to Taltech server", skin);
        settingButton = new TextButton("Settings", skin);
        statsButton = new TextButton("Statistics", skin);
        exitButton = new TextButton("Exit game", skin);

        // Add listeners to buttons
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Start the game when start button is clicked
                GameScreen.setPlayerNicknameTemp(nicknameField.getText());
                Gdx.app.postRunnable(new SetScreenRunnable(ScreenManager.ScreenEnum.GAME));
            }
        });

        lobbiesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Open lobby select menu
                if (true) {
                    NetworkVariables.setDefaultVariables();
                    NetworkVariables.setPlayerNicknameTemp(nicknameField.getText());
                    NetworkVariables.replaceConnection();
                }
                Gdx.app.postRunnable(new SetScreenRunnable(ScreenManager.ScreenEnum.LOBBIES));
                // dispose();
            }
        });

        taltechButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Open lobby select menu
                String oldHost = NetworkVariables.getHost();
                NetworkVariables.setTaltechVariables();
                NetworkVariables.setPlayerNicknameTemp(nicknameField.getText());
                if (!oldHost.equals(NetworkVariables.getHost())) {
                    NetworkVariables.replaceConnection();
                }
                Gdx.app.postRunnable(new SetScreenRunnable(ScreenManager.ScreenEnum.LOBBIES));
                // dispose();
            }
        });

        settingButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Open lobby select menu
                Gdx.app.postRunnable(new SetScreenRunnable(ScreenManager.ScreenEnum.SETTINGS));
                // dispose();
            }
        });

        statsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Open lobby select menu
                Gdx.app.postRunnable(new SetScreenRunnable(ScreenManager.ScreenEnum.STATS));
                // dispose();
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Exit the game when exit button is clicked
                ScreenManager.exitGame();
            }
        });

        table.add(gameName).growX().fillX().expandX().center().padBottom(300).row();

        table.add(lobbiesButton).width(300).padBottom(10).row();
        table.add(taltechButton).width(300).padBottom(10).row();
        table.add(settingButton).width(300).padBottom(10).row();
        table.add(statsButton).width(300).padBottom(10).row();
        table.add(exitButton).width(300).padBottom(10).row();
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
        stage.dispose(); // Dispose the stage resources when the screen is disposed
    }
}
