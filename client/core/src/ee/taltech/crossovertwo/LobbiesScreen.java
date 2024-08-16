package ee.taltech.crossovertwo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ee.taltech.crossovertwo.packets.PacketLobbies;
import ee.taltech.crossovertwo.runnables.SetScreenRunnable;
import ee.taltech.crossovertwo.utilities.GraphicVariables;
import ee.taltech.crossovertwo.utilities.MenuBackground;
import ee.taltech.crossovertwo.utilities.NetworkVariables;
import ee.taltech.crossovertwo.utilities.ScreenManager;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class LobbiesScreen extends ScreenAdapter {

    private crossovertwo game;
    private static Client client = NetworkVariables.getClient();
    private Stage stage;
    private TextField serverNameField;
    private TextButton getLobbiesButton, createServerButton, backToMenuButton;
    private List<String> serverList;

    public static boolean morbinTime = false;
    public static Listener.ThreadedListener lobbiesListener;

    private Table table = new Table();
    private BitmapFont font = new BitmapFont();
    private Viewport viewport;
    private Skin skin;
    private Label serverLabel;
    private Table tableScrolling;

    /**
     * Constructor for the LobbiesScreen
     * @param game The game object
     */
    public LobbiesScreen(crossovertwo game) {
        this.game = game;
    }

    /**
     * Method to show the LobbiesScreen
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
        // Initialize a table for UI layout

        table.clear();

        table.setFillParent(true);
        table.setBackground(GraphicVariables.getGeneralBackground());
        table.center().pad(100).padLeft(200).padRight(200);
        table.defaults().pad(5);

        tableScrolling = new Table(skin);
//        tableScrolling.setDebug(true);

        ScrollPane scrollPane = new ScrollPane(tableScrolling, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false); // Enable only vertical scrolling

        table.add(scrollPane).expand().fill().colspan(2).row();
        stage.addActor(table);
        stage.setScrollFocus(scrollPane);

        lobbiesListener = new Listener.ThreadedListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Map) {
                    System.out.println(object);
                    String type = ((List<String>) ((Map<?, ?>) object).get("type")).get(0);
                    switch (type) {
                        case "lobbyGetRooms":
                            serverList = (List<String>) ((Map<?, ?>) object).get("info");
                            Gdx.app.postRunnable(() -> updateServerList());
                            break;
                        case "LobbyPlayers":
                            System.out.println(((Map<?, ?>) object).get("info"));
                            ScreenManager.lobbyScreen.setConnectedPlayers((List<String>) ((Map<?, ?>) object).get("info"));
                            int lobbyHost = Integer.parseInt(((List<String>) ((Map<?, ?>) object).get("host")).get(0));
                            System.out.println(((Map<?, ?>) object).get("host"));
                            System.out.println(lobbyHost);
                            ScreenManager.lobbyScreen.setHostId(lobbyHost);
                            break;
                        case "connectionFail":
                            System.out.println("failed");
                            break;
                        case "LobbyClientStartGame":
                            System.out.println("We're getting somewhere");
                            morbinTime = true;
                            break;
                    }
                }
            }
        });

        client.addListener(lobbiesListener);

        // Initialize UI elements
        getLobbiesButton = new TextButton("Get lobbies", skin);
        createServerButton = new TextButton("Create lobby", skin);
        backToMenuButton = new TextButton("Back to menu", skin);
        serverNameField = new TextField("", skin);
        serverNameField.setMessageText("Server name here");
        serverLabel = new Label("Server name: ", GraphicVariables.getLabelStyle());

        // send get lobbies
        PacketLobbies.sendGetRoom();

        System.out.println(serverList);

        getLobbiesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Get lobbies
                System.out.println(client);
                PacketLobbies.sendGetRoom();
            }
        });


        createServerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Get lobbies
                String serverName = serverNameField.getText();
                if (serverName.isBlank() || serverList.contains(serverName)) {
                    Random random = new Random();
                    serverName = String.valueOf(random.nextInt(0, Integer.MAX_VALUE));
                }
                PacketLobbies.sendCreateRoom(serverName);
            }
        });

        backToMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Get lobbies
                Gdx.app.postRunnable(new SetScreenRunnable(ScreenManager.ScreenEnum.MENU));
            }
        });

        renderGetCreateButtons();
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
     * Method to update the server list
     * This method clears the table and updates the server list
     */
    public void updateServerList() {
        tableScrolling.clear();
        if (serverList != null) {
            for (String server : serverList) {
                TextButton serverButton = new TextButton("Server: " + server, skin);

                serverButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        // Get lobbies
                        PacketLobbies.sendGetRoom();
                        if (serverList.contains(server)) {
                            PacketLobbies.sendConnectLobbyTo(server);
                            ScreenManager.lobbyScreen.setServerNameField(server);
                            Gdx.app.postRunnable(new SetScreenRunnable(ScreenManager.ScreenEnum.LOBBY));
                        }
                    }
                });

                tableScrolling.add(serverButton).height(20).width(300).pad(5);
                tableScrolling.row();
            }

        }
    }

    /**
     * Method to render the get and create buttons
     * This method adds the get and create buttons to the table
     */
    private void renderGetCreateButtons() {
        table.add(getLobbiesButton).height(40).expandX().fillX().colspan(2);
        table.row();
        table.add(createServerButton).height(40).expandX().fillX().colspan(2);
        table.row();
        table.add(serverLabel).padRight(10);
        table.add(serverNameField).height(40).expandX().fillX();
        table.row();
        table.add(backToMenuButton).height(40).expandX().fillX().colspan(2);
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

        createServerButton.setDisabled(serverNameField.getText().length() > 15);

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
        font.dispose();
    }

    /**
     * Method to get the client
     * @return The client
     */
    public static Client getClient() {
        return client;
    }

    /**
     * Method to set the client
     * @param client The client
     */
    public static void setClient(Client client) {
        LobbiesScreen.client = client;
    }

}
