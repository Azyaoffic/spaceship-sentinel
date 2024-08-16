package ee.taltech.crossovertwo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ee.taltech.crossovertwo.utilities.GraphicVariables;
import ee.taltech.crossovertwo.utilities.NetworkVariables;
import ee.taltech.crossovertwo.utilities.PreferencesSaver;
import ee.taltech.crossovertwo.utilities.ScreenManager;

public class crossovertwo extends Game {

    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    BitmapFont font;

    /**
     * Method to create the game
     * This method initializes the SpriteBatch, ShapeRenderer and BitmapFont
     * It also sets the MenuScreen as the current screen
     */
    @Override
    public void create () {
        GraphicVariables.updateFonts();
        NetworkVariables.createConnection();
        PreferencesSaver.initialize();
        Score.initialize();

        ScreenManager.setup(this);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();

        ScreenManager.changeScreen(ScreenManager.ScreenEnum.MENU);
    }

    /**
     * Method to render the game
     * This method calls the render method of the current screen
     */
    @Override
    public void dispose () {
        super.dispose();
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }
}