package ee.taltech.crossovertwo.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public abstract class MenuBackground {

    private static Texture background1, background2;
    private static SpriteBatch batch;
    private static float yMax, yCoordBg1, yCoordBg2;
    private static final int BACKGROUND_MOVE_SPEED = 100;
    private static ExtendViewport viewportBackground;
    private static OrthographicCamera camera;

    /**
     * Initialize the background.
     */
    public static void initialize() {
        background1 = new Texture(Gdx.files.internal("menuPreview.png"));
        background2 = new Texture(Gdx.files.internal("menuPreview.png")); // identical
        yMax = 1213; // height of the background image
        yCoordBg1 = -yMax; yCoordBg2 = 0;
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, 0, 1000);
        viewportBackground = new ExtendViewport(800, 800, camera);
    }

    /**
     * Render the background.
     */
    public static void render() {
        viewportBackground.apply();
        yCoordBg1 += BACKGROUND_MOVE_SPEED * Gdx.graphics.getDeltaTime();
        yCoordBg2 = yCoordBg1 + yMax;  // We move the background, not the camera
        if (yCoordBg1 >= 0) {
            yCoordBg1 = -yMax; yCoordBg2 = 0;
        }
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background2, -background2.getWidth() / 2f, yCoordBg2);
        batch.draw(background1, -background1.getWidth() / 2f, yCoordBg1);
        batch.end();
    }

    /**
     * Dispose of the background.
     */
    public static void dispose() {
        background1.dispose();
        background2.dispose();
        batch.dispose();
    }

    /**
     * Resize the background.
     * @param width The new width
     * @param height The new height
     */
    public static void resize(int width, int height) {
        viewportBackground.update(width, height);
    }
}
