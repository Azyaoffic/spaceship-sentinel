package ee.taltech.crossovertwo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import ee.taltech.crossovertwo.runnables.SetScreenRunnable;
import ee.taltech.crossovertwo.utilities.*;

import java.util.HashMap;
import java.util.Map;

public class SettingScreen extends ScreenAdapter {

    private Map<ControlKeys, Integer> keyMap = new HashMap<>();
    private static String openaiKey;

    private crossovertwo game;
    private Stage stage;
    private FitViewport viewport;
    private ScrollPane scrollPane;
    private Table table;
    private Skin skin;
    private boolean isWaitingForKey = false;
    private TextButton buttonKeyAwaiting;
    private ControlKeys keyAwaiting;
    private boolean fullscreen = false;
    private TextField openaiField;

    /**
     * @param game The game instance
     */
    public SettingScreen(crossovertwo game) {
        this.game = game;
        if (PreferencesSaver.isPreferencesEmpty()) {
            loadDefaultKeyMap();
        } else {
            keyMap = PreferencesSaver.loadAllPreferences();
            openaiKey = PreferencesSaver.loadOpenAIKey();
        }
    }

    /**
     * Method to load the default key map
     * This method loads the default key map
     */
    private void loadDefaultKeyMap() {
        keyMap.put(ControlKeys.UP, Input.Keys.W);
        keyMap.put(ControlKeys.DOWN, Input.Keys.S);
        keyMap.put(ControlKeys.LEFT, Input.Keys.A);
        keyMap.put(ControlKeys.RIGHT, Input.Keys.D);
        keyMap.put(ControlKeys.SHOOT, Input.Buttons.LEFT);
        keyMap.put(ControlKeys.INVENTORY, Input.Keys.TAB);
        keyMap.put(ControlKeys.CRAFT, Input.Keys.Q);
        keyMap.put(ControlKeys.UNARMED, Input.Keys.NUM_1);
        keyMap.put(ControlKeys.WEAPON, Input.Keys.NUM_2);
        keyMap.put(ControlKeys.HEAL, Input.Keys.NUM_3);
        keyMap.put(ControlKeys.TURRET, Input.Keys.NUM_4);
        PreferencesSaver.saveAllPreferences(keyMap);
    }

    /**
     * The stage for the setting screen
     */
    @Override
    public void show() {
        System.out.print("OpenAI Key: ");
        System.out.println(openaiKey);
        openaiKey = PreferencesSaver.loadOpenAIKey();

        if (PreferencesSaver.isPreferencesEmpty()) {
            loadDefaultKeyMap();
        } else {
            keyMap = PreferencesSaver.loadAllPreferences();
        }

        viewport = new FitViewport(800, 800);
        skin = GraphicVariables.getSkin();
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        Table mainTable = new Table();
        // mainTable.setDebug(true);
        mainTable.setFillParent(true);
        mainTable.setBackground(GraphicVariables.getGeneralBackground());
        mainTable.center().pad(20);
        mainTable.defaults().pad(5);
        stage.addActor(mainTable); // Add table to the stage

        Label titleLabel = new Label("Settings", GraphicVariables.getLabelStyleForLabels());

        Button resetButton = new TextButton("Reset", skin);
        Button saveButton = new TextButton("Save", skin);
        Button backButton = new TextButton("Back to menu", skin);

        resetButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                loadDefaultKeyMap();
                updateKeyMap();
            }
        });

        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                PreferencesSaver.saveAllPreferences(keyMap);
                PreferencesSaver.saveOpenAIKey(openaiField.getText());
                updateKeyMap();
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.postRunnable(new SetScreenRunnable(ScreenManager.ScreenEnum.MENU));
            }
        });

        table = new Table(skin);
        // table.setDebug(true);

        updateKeyMap();

        scrollPane = new ScrollPane(table, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false); // Enable only vertical scrolling

        mainTable.add(titleLabel).row();
        mainTable.add(scrollPane).expand().fill().row();
        mainTable.add(resetButton).height(40).width(300).row();
        mainTable.add(saveButton).height(40).width(300).row();
        mainTable.add(backButton).height(40).width(300);
        stage.setScrollFocus(scrollPane);

        scrollPane.setTouchable(Touchable.enabled);
    }

    /**
     * Method to update the key map
     * This method updates the key map with the new key
     */
    private void updateKeyMap() {
        openaiKey = PreferencesSaver.loadOpenAIKey();
        table.clear();
        for (ControlKeys key : ControlKeys.values()) {
            Label labelKey = new Label(String.valueOf(key), GraphicVariables.getLabelStyle());
            labelKey.setAlignment(Align.center, Align.left);
            TextButton buttonKey = new TextButton(String.valueOf(getButtonKey(keyMap.get(key))), skin);

            buttonKey.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    buttonKey.setText("Press a key");
                    isWaitingForKey = true;
                    buttonKeyAwaiting = buttonKey;
                    keyAwaiting = key;
                }
            });

            table.add(labelKey);
            table.add(buttonKey).height(40).width(300).pad(5).growX();
            table.row();
        }

        TextButton fullscreenButton = new TextButton("Set to Fullscreen", skin);
        Label fullscreenLabel = new Label("Switch windowed/fullscreen", GraphicVariables.getLabelStyle());

        table.add(fullscreenLabel);
        table.add(fullscreenButton).height(40).width(300).pad(5).growX();
        table.row();

        fullscreenButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                fullscreen = !fullscreen;
                if (fullscreen) fullscreenButton.setText("Set to Windowed");
                else fullscreenButton.setText("Set to Fullscreen");
                toggleFullscreen(fullscreen);
            }
        });

        Label openaiLabel = new Label("OpenAI API Key", GraphicVariables.getLabelStyle());
        openaiField = new TextField(openaiKey, skin);

        table.add(openaiLabel);
        table.add(openaiField).height(40).width(300).pad(5).growX();
    }

    /**
     * Method to toggle fullscreen
     * This method toggles the fullscreen mode
     * @param fullscreen The fullscreen mode to toggle
     */
    public void toggleFullscreen(boolean fullscreen) {
        if (fullscreen) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(800, 800);
        }
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

        if (isWaitingForKey) {
            Gdx.input.setInputProcessor(new InputAdapter() {
                @Override
                public boolean keyDown(int keycode) {
                    if (keyMap.containsValue(keycode)) return true;
                    if (keycode == Input.Keys.ESCAPE) {
                        buttonKeyAwaiting.setText(getButtonKey(keyMap.get(keyAwaiting)));
                        isWaitingForKey = false;
                        buttonKeyAwaiting = null;
                        keyAwaiting = null;
                        Gdx.input.setInputProcessor(stage);
                        return true;
                    }
                    keyMap.put(keyAwaiting, keycode);
                    buttonKeyAwaiting.setText(Input.Keys.toString(keycode));
                    buttonKeyAwaiting.setColor(1, 0, 0, 1);
                    isWaitingForKey = false;
                    buttonKeyAwaiting = null;
                    keyAwaiting = null;
                    Gdx.input.setInputProcessor(stage);
                    return true;
                }
            });
        }

        MenuBackground.render();

        viewport.apply();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    /**
     * Method to get the key string
     * This method returns the key string for the given key
     * @param key The key to get the string for
     * @return The key string
     */
    private String getButtonKey(int key) {
        String keyString = Input.Keys.toString(key);
        if (keyString.equalsIgnoreCase("unknown")) {
            switch (key) {
                case 0 -> keyString = "Left Mouse Button";
                case 1 -> keyString = "Right Mouse Button";
                case 2 -> keyString = "Middle Mouse Button";
                case 3 -> keyString = "Back Mouse Button";
                case 4 -> keyString = "Forward Mouse Button";
                default -> keyString = "Unknown Key";
            }
        }
        return keyString;
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
            stage.dispose(); // Dispose the stage resources when the screen is disposed
        } catch (Exception ignored) { }
    }

}
