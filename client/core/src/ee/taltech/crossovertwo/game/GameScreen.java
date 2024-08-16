package ee.taltech.crossovertwo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ee.taltech.crossovertwo.Score;
import ee.taltech.crossovertwo.crossovertwo;
import ee.taltech.crossovertwo.game.HUD.hud.HUD;
import ee.taltech.crossovertwo.game.HUD.ingamemenu.InGameMenu;
import ee.taltech.crossovertwo.game.HUD.messages.Message;
import ee.taltech.crossovertwo.game.bullet.Bullet;
import ee.taltech.crossovertwo.game.enemies.Enemy;
import ee.taltech.crossovertwo.game.items.bot.BotPlayer;
import ee.taltech.crossovertwo.game.mothership.Mothership;
import ee.taltech.crossovertwo.game.players.Player;
import ee.taltech.crossovertwo.game.players.PlayerOther;
import ee.taltech.crossovertwo.game.resources.ResourceGenerator;
import ee.taltech.crossovertwo.game.trader.Trader;
import ee.taltech.crossovertwo.game.turrets.Turret;
import ee.taltech.crossovertwo.packets.PacketGameEnd;
import ee.taltech.crossovertwo.utilities.OpenaiAPI;
import ee.taltech.crossovertwo.utilities.PreferencesSaver;
import ee.taltech.crossovertwo.utilities.ScreenManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class GameScreen extends ScreenAdapter {
    private static crossovertwo game;
    private static String host = "localhost";
    private static Integer tcpPort = 8080;
    private static Integer udpPort = 8081;
    private static boolean isEnded = false;
    private static String playerNicknameTemp;
    public OrthographicCamera camera;

    List<Long> spawnedAtMinutes = new ArrayList<>();
    public static Player myPlayer;
    public static Client client;
    public static int clientId;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    public static final int WORLD_WIDTH = 3200, WORLD_HEIGHT = 3200;
    private Array<RectangleMapObject> worldObjects;
    public Mothership mothership = new Mothership();
    public static Map<Integer, Map<String, String>> serverTime;
    public static Viewport gamePort;
    public static Viewport hudPort;
    private static final String openaiKey = PreferencesSaver.loadOpenAIKey();
    private static final String wanderingTraderNotif = OpenaiAPI.chatGPT(OpenaiAPI.prompt, openaiKey);

    public static void setClient(Client client) {
        GameScreen.client = client;
    }

    /**
     * Method to set the player nickname
     * @param playerNicknameTemp The player nickname
     */
    public static void setPlayerNicknameTemp(String playerNicknameTemp) {
        GameScreen.playerNicknameTemp = playerNicknameTemp;
    }

    /**
     * Constructor for the GameScreen
     * @param game The game object
     */
    public GameScreen(crossovertwo game) {
        this.game = game;
        isEnded = false;
    }

    /**
     * Method to connect to the server
     * This method is called when the game screen is shown
     */
    private void cameraSetup() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gamePort = new ExtendViewport(800, 800, camera);
        hudPort = new FitViewport(800, 800);
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(myPlayer.getxCoord(), myPlayer.getyCoord(), 0);
        camera.zoom = 0.7f;
        camera.update();
    }

    /**
     * Method to spawn enemy waves on a timer
     * @param secondsInBetween The amount of seconds in between each wave
     * @param enemyAmount The amount of enemies to spawn in each wave
     */
    public void spawnEnemyWaveOnTimer(int secondsInBetween, int enemyAmount) {
        if (serverTime != null) {
            long millis = Long.parseLong(serverTime.get(-10).get("endTime")) - Long.parseLong(serverTime.get(-10).get("currentTime"));

            if (millis < 30) {
                PacketGameEnd.sendGameWin();
            }

            long secondsLeft = TimeUnit.MILLISECONDS.toSeconds(millis);
            if (!spawnedAtMinutes.contains(secondsLeft) && secondsLeft % secondsInBetween == 0) {
                spawnedAtMinutes.add(secondsLeft);
                Enemy.spawnEnemyWave(enemyAmount);
                // Let's also heal mothership a bit
                mothership.healMothership(200);
                myPlayer.revive();

                HUD.wave++;
                HUD.updateWave();
            }
        }
    }

    /**
     * Method to show the GameScreen
     * This method initializes the client and connects to the server
     * It also initializes the map and the player
     * This method is called when the screen is shown
     */
    @Override
    public void show() {
        System.out.println(wanderingTraderNotif);

        Enemy.createTextures();
        Enemy.spawnEnemyWave(5);

        map = new TmxMapLoader().load("maps/GameMap.tmx");
        worldObjects = map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class);
        renderer = new OrthogonalTiledMapRenderer(map);

        myPlayer = new Player(client, worldObjects, playerNicknameTemp, mothership);

        cameraSetup();

        Score.getInstance().resetLocalScore();

        HUD.initialize();
        HUD.setNickname(playerNicknameTemp);

        InGameMenu.initialize();

        myPlayer.createInventory();

        clientId = client.getID();

        client.addListener(new Listener.ThreadedListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Map) {
                    String type = ((Map<Integer, Map<String, String>>) object).get(0).get("type");
                    switch (type) {
                        case "nickname":
                            Gdx.app.postRunnable(() -> PlayerOther.addNickname((Map<Integer, Map<String, String>>) object));
                            break;
                        case "playercoords":
                            Gdx.app.postRunnable(() -> PlayerOther.saveReceivedPlayers((Map<Integer, Map<String, String>>) object));
                            break;
                        case "status":
                            Gdx.app.postRunnable(() -> PlayerOther.saveReceivedPlayersStatus((Map<Integer, Map<String, String>>) object));
                            break;
                        case "bullet":
                            Gdx.app.postRunnable(() -> Bullet.receivedBullets = ((Map<Integer, Map<String, String>>) object));
                            break;
                        case "enemies":
                            break;
                        case "enemiesList":
                            List<Map<String, String>> enemies = (List<Map<String, String>>) ((Map<Integer, Map<String, String>>) object).get(-5);
                            Gdx.app.postRunnable(() -> Enemy.addReceivedEnemiesList(enemies));
                            break;
                        case "enemiesClear":
                            Enemy.enemies.clear();
                            Enemy.addReceivedEnemiesList(List.of());
                            Enemy.dispose();
                            break;
                        case "turretsClear":
                            Turret.turrets.clear();
                            Turret.addTurretToList(List.of());
                            Turret.dispose();
                            break;
                        case "time":
                            serverTime = (Map<Integer, Map<String, String>>) object;
                            break;
                        case "mothershiphp":
                            int newHp = Integer.parseInt(((Map<Integer, Map<String, String>>) object).get(0).get("mothershiphp"));
                            mothership.setHealth(newHp);
                            break;
                        case "weapon":
                            Gdx.app.postRunnable(() -> PlayerOther.addWeapon((Map<Integer, Map<String, String>>) object));
                            break;
                        case "gameend":
                            System.out.println(object);
                            if (Boolean.parseBoolean(((Map<Integer, Map<String, String>>) object).get(0).get("win"))) {
                                Gdx.app.postRunnable(GameScreen::endWinGame);
                            } else {
                                Gdx.app.postRunnable(GameScreen::endGame);
                            }
                            break;
                        case "generatorsList":
                            List<Map<String, String>> generators = (List<Map<String, String>>) ((Map<Integer, Map<String, String>>) object).get(-5);
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    List<ResourceGenerator> generatorList = new ArrayList<>();

                                    for (Map<String, String> generatorMap : generators) {
                                        generatorList.add(new ResourceGenerator(
                                                Integer.parseInt(generatorMap.get("xCoord")),
                                                Integer.parseInt(generatorMap.get("yCoord")),
                                                Integer.parseInt(generatorMap.get("radius")),
                                                generatorMap.get("textureName"),
                                                generatorMap.get("resourceToSpawnName")
                                        ));
                                    }
                                    ResourceGenerator.setGenerators(generatorList);
                                }
                            });
                            break;
                        case "turretsList":
                            List<Map<String, String>> turrets = (List<Map<String, String>>) ((Map<Integer, Map<String, String>>) object).get(-5);
                            if (turrets.size() > 0) {
                                Gdx.app.postRunnable(() -> Turret.addTurretToList(turrets));
                            } else {
                                Gdx.app.postRunnable(() -> Turret.turrets.clear());
                            }
                            break;
                        case "botsList":
                            List<Map<String, String>> bots = (List<Map<String, String>>) ((Map<Integer, Map<String, String>>) object).get(-5);
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    List<BotPlayer> botList = new ArrayList<>();

                                    for (Map<String, String> botMap : bots) {
                                        botList.add(new BotPlayer(
                                                Integer.parseInt(botMap.get("health")),
                                                Integer.parseInt(botMap.get("damage")),
                                                Integer.parseInt(botMap.get("speed")),
                                                Integer.parseInt(botMap.get("x")),
                                                Integer.parseInt(botMap.get("y")),
                                                Integer.parseInt(botMap.get("targetEnemyId"))
                                        ));
                                    }
                                    BotPlayer.setBotPlayers(botList);
                                }
                            });
                            break;
                    }
                }
            }
        }));

    }

    /**
     * Method to resize the game screen
     * This method is called when the game window is resized
     * @param width The new width of the window
     * @param height The new height of the window
     */
    @Override
    public void resize(int width, int height) {
        hudPort.update(width, height);
        gamePort.update(width,height);
    }

    /**
     * Method to render the game screen
     * This method is called every frame to render the game
     * @param delta The time in seconds since the last render
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.42f, 0.51f, 0.63f, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        spawnEnemyWaveOnTimer(20, 3);
        Enemy.incrementGlobalTimer();

        renderer.setView(camera);
        renderer.render();

        ResourceGenerator.render(camera, myPlayer);

        PlayerOther.render(camera);
        BotPlayer.render(camera, myPlayer);

        Bullet.render(camera);

        Enemy.render(camera, myPlayer);

        mothership.render(camera, myPlayer);

        Turret.render(camera, myPlayer);

        // Render the player
        myPlayer.draw(camera);

        if (Player.getHp() <= Player.HP / 2 && !Trader.isTraderActive) {
            Trader.spawnTrader(worldObjects);
            Message.showMessage(wanderingTraderNotif);
        } else if (Player.getHp() > Player.HP / 2 && Trader.isTraderActive) {
            Trader.despawnTrader();
            Message.showMessage("Trader has disappeared!");
        }

        if (Trader.isTraderActive) Trader.render(camera, myPlayer);

        HUD.render();
    }

    /**
     * Method to end the game
     * This method is called when the game is over
     */
    public static void endGame() {
        if (isEnded) {
            return;
        }
        Score.getInstance().endGame();
        ScreenManager.changeScreen(ScreenManager.ScreenEnum.GAMEOVER);
        resetSomeVariables();
        isEnded = true;
        client.stop();
    }

    /**
     * Method to end the game when the game is won
     * This method is called when the game is won
     */
    public static void endWinGame() {
        if (isEnded) {
            return;
        }
        Score.getInstance().endGame();
        ScreenManager.changeScreen(ScreenManager.ScreenEnum.WIN);
        resetSomeVariables();
        isEnded = true;
        client.stop();
    }

    /**
     * Method to reset some variables
     * This method resets some variables when the game is over
     */
    public static void resetSomeVariables() {
        serverTime = null;
        HUD.wave = 0;
        ResourceGenerator.setGenerators(null);
        Player.clearPlayerResources();
    }

    /**
     * Method to dispose of the game screen
     * This method is called when the game screen is no longer needed
     */
    @Override
    public void dispose() {
        Bullet.dispose();
        Enemy.dispose();
        mothership.dispose();
        try {
            myPlayer.dispose();
            map.dispose();
            renderer.dispose();
            HUD.dispose();
        } catch (Exception e) {
            System.out.println("GameScreen disposed without game started");
        }
    }

    /**
     * Method to hide the game screen
     * This method is called when the game screen is hidden
     */
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }
}
