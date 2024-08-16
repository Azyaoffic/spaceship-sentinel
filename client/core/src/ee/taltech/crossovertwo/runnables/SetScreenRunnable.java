package ee.taltech.crossovertwo.runnables;

import ee.taltech.crossovertwo.utilities.ScreenManager;
public class SetScreenRunnable implements Runnable {

    private ScreenManager.ScreenEnum screen;

    /**
     * Constructor for the SetScreenRunnable
     * @param screen The screen to set
     */
    public SetScreenRunnable(ScreenManager.ScreenEnum screen) {
        this.screen = screen;
    }

    @Override
    public void run() {
        ScreenManager.changeScreen(screen);
    }
}
