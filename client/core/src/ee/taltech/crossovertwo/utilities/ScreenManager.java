package ee.taltech.crossovertwo.utilities;

import ee.taltech.crossovertwo.*;
import ee.taltech.crossovertwo.game.GameOverScreen;
import ee.taltech.crossovertwo.game.GameScreen;
import ee.taltech.crossovertwo.game.WinScreen;
import ee.taltech.crossovertwo.game.turrets.Turret;
import ee.taltech.crossovertwo.packets.Packet;

public class ScreenManager {

    // Screens
    private static MenuScreen menuScreen;
    public static LobbiesScreen lobbiesScreen;
    public static LobbyScreen lobbyScreen;
    public static GameScreen gameScreen;
    public static GameOverScreen gameOverScreen;
    public static WinScreen winScreen;
    private static crossovertwo game;
    private static SettingScreen settingScreen;
    private static StatsScreen statsScreen;

    public enum ScreenEnum {
            MENU,
            LOBBIES,
            LOBBY,
            GAME,
            GAMEOVER,
            WIN,
            SETTINGS,
            STATS
    }

    /**
     * Setup for ScreenManager
     * @param game game object of crossovertwo
     */
    public static void setup(crossovertwo game) {
        ScreenManager.game = game;
        createScreens();

        game.setScreen(menuScreen);
    }

    /**
     * Change actual screen.
     * @param screenEnum switch to which screen
     */
    public static void changeScreen(ScreenEnum screenEnum) {
        switch (screenEnum) {
            case MENU:
                game.setScreen(menuScreen);
                break;
            case LOBBIES:
                game.setScreen(lobbiesScreen);
                break;
            case LOBBY:
                game.setScreen(lobbyScreen);
                break;
            case GAME:
                game.setScreen(gameScreen);
                break;
            case GAMEOVER:
                game.setScreen(gameOverScreen);
                break;
            case WIN:
                game.setScreen(winScreen);
                break;
            case SETTINGS:
                game.setScreen(settingScreen);
                break;
            case STATS:
                game.setScreen(statsScreen);
                break;
        }
    }

    /**
     * Dispose all screens and create new.
     * When game is ended the game cycle started again.
     */
    public static void reset() {
        dispose();

        NetworkVariables.replaceConnection();
        GameScreen.resetSomeVariables();
        Turret.dispose();

        createScreens();

        LobbiesScreen.setClient(NetworkVariables.getClient());
        Packet.updateClient(NetworkVariables.getClient());
        GraphicVariables.updateFonts();
        GraphicVariables.updateTextures();

        game.setScreen(menuScreen);
    }

    /**
     * Dispose all screens.
     */
    public static void dispose() {
        menuScreen.dispose();
        lobbiesScreen.dispose();
        lobbyScreen.dispose();
        gameScreen.dispose();
        gameOverScreen.dispose();
        winScreen.dispose();
        settingScreen.dispose();
        statsScreen.dispose();
        MenuBackground.dispose();
    }

    /**
     * Exit the game.
     */
    public static void exitGame() {
        System.exit(0);
    }

    /**
     * Create all screens.
     */
    private static void createScreens() {
        MenuBackground.initialize();
        menuScreen = new MenuScreen(game);
        lobbiesScreen = new LobbiesScreen(game);
        lobbyScreen = new LobbyScreen(game);
        gameScreen = new GameScreen(game);
        gameOverScreen = new GameOverScreen(game);
        winScreen = new WinScreen(game);
        settingScreen = new SettingScreen(game);
        statsScreen = new StatsScreen(game);
    }
}