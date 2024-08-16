package ee.taltech.crossovertwo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryonet.Client;
import ee.taltech.crossovertwo.game.GameScreen;
import ee.taltech.crossovertwo.packets.PacketLobbies;
import ee.taltech.crossovertwo.utilities.GraphicVariables;
import ee.taltech.crossovertwo.utilities.MenuBackground;
import ee.taltech.crossovertwo.utilities.NetworkVariables;
import ee.taltech.crossovertwo.utilities.ScreenManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobbyScreen extends ScreenAdapter {

    private crossovertwo game;
    private Client client = NetworkVariables.getClient();
    private Stage stage;
    private String serverNameField = "None";
    private TextButton startGameButton;
    private List<Integer> connectedPlayers = new ArrayList<>();
    private Map<Integer, String> nicknames = new HashMap<>();
    private int hostId;

    private Table table = new Table();
    private Table tableMain = new Table();
    private Table tableOtherPlayers = new Table();
    private BitmapFont font = new BitmapFont();

    private Viewport viewport;
    private Skin skin;

    private TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();

    private boolean hasCreatedHostButton = false;

    /**
     * Constructor for the LobbiesScreen
     * @param game The game object
     */
    public LobbyScreen(crossovertwo game) {
        this.game = game;

        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.WHITE;
    }

    /**
     * Method to create the UI
     * This method creates the UI elements and adds them to the stage
     */
    private void createUI() {

        System.out.println("Create " + nicknames);

        TextField playerNickname = new TextField("", skin);
        playerNickname.setMessageText("Player"+ client.getID());
        table.add(playerNickname);
        TextButton save = new TextButton("Save", skin);
        table.add(save).row();
        TextButton backToMenuButton = new TextButton("Back to menu", skin);
        table.add(backToMenuButton).colspan(2).row();

        backToMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.reset();
            }
        });

        save.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!playerNickname.getText().isBlank()) {
                    PacketLobbies.sendNickname(playerNickname.getText(), serverNameField);
                }
            }
        });

    }

    /**
     * Method to set the server name field
     * @param serverNameField The server name
     */
    public void setServerNameField(String serverNameField) {
        // Create the Label with the font
        this.serverNameField = serverNameField;
        table.add(new Label(serverNameField, GraphicVariables.getLabelStyleForLabels())).colspan(2).pad(10).row();
    }

    /**
     * Method to set the connected players
     * @param connectedPlayers The connected players
     */
    public void setConnectedPlayers(List<String> connectedPlayers) {
        this.connectedPlayers.clear();
        for (int i = 0; i < connectedPlayers.size(); i += 2) {
            nicknames.put(Integer.parseInt(connectedPlayers.get(i)), connectedPlayers.get(i + 1));
            this.connectedPlayers.add(Integer.parseInt(connectedPlayers.get(i)));
        }

        tableOtherPlayers.clear();
        tableOtherPlayers.center();
        tableOtherPlayers.add(new Label("Connected players", GraphicVariables.getLabelStyleForLabels())).row();
        tableOtherPlayers.row();
        for (int player : this.connectedPlayers) {
            tableOtherPlayers.add(new Label(nicknames.get(player), GraphicVariables.getLabelStyle())).row();
            System.out.println("Adding player " + nicknames.get(player));
        }
    }

    /**
     * Method to make the start button visible
     */
    private void makeStartButtonVisible() {
        System.out.println("Your id: " + client.getID() + " host id: " + hostId);
        if (client.getID() == hostId || client.getID() == 1) {
            table.add(startGameButton).colspan(2).pad(10).row();
            hasCreatedHostButton = true;
        }
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

        skin = GraphicVariables.getSkin();

        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        tableMain.setFillParent(true);
        tableMain.setBackground(GraphicVariables.getGeneralBackground());
        table.center();
        table.defaults().fill().pad(10).height(30);

//         tableMain.setDebug(true);
//         tableOtherPlayers.setDebug(true);

        startGameButton = new TextButton("Start Game", skin);
        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("ServerName: " + serverNameField);
                PacketLobbies.sendStartGame(serverNameField);
            }
        });

        table.center();

        tableMain.pad(50);
        tableMain.defaults().grow();
        tableMain.add(table);
        tableMain.add(tableOtherPlayers);
        stage.addActor(tableMain);

        client = ScreenManager.lobbiesScreen.getClient();
        // Initialize a table for UI layout
        createUI();
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
        if (LobbiesScreen.morbinTime) {
            LobbiesScreen.morbinTime = false;
            System.out.println("it's gamin time");
            client.removeListener(LobbiesScreen.lobbiesListener);
            GameScreen.setClient(client);
            GameScreen.setPlayerNicknameTemp(nicknames.get(client.getID()));
            ScreenManager.changeScreen(ScreenManager.ScreenEnum.GAME);
            return;
        }

        if (!hasCreatedHostButton) {
            System.out.println("Creating start button");
            makeStartButtonVisible();
        }

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

    /**
     * Method to set the host id
     * @param hostId The host id
     */
    public void setHostId(int hostId) {
        this.hostId = hostId;
    }
}
