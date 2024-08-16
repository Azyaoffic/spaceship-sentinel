package ee.taltech.crossovertwo.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import ee.taltech.crossovertwo.game.bullet.Bullet;
import ee.taltech.crossovertwo.game.enemies.Enemy;
import ee.taltech.crossovertwo.game.turrets.Turret;

public class GraphicVariables {

    private static BitmapFont generalFont;
    private static BitmapFont generalFontForLabels;
    private static BitmapFont generalFontForMessages;
    private static TextureRegionDrawable generalBackground;
    private static Label.LabelStyle labelStyle, labelStyleForLabels, nameStyle;
    private static Skin skin;

    // Resource Generator Textures
    private static Texture generatorCoal = new Texture("coal.png");
    private static Texture generatorIron = new Texture("iron.png");
    private static Texture generatorGold = new Texture("gold.png");

    public static GlyphLayout layout = new GlyphLayout();

    /**
     * Update the textures of the game.
     */
    public static void updateTextures() {
        Bullet.updateTextures();
        Turret.updateTextures();
    }

    /**
     * Get the texture of the generator.
     * @param type The type of the generator
     * @return The texture of the generator
     */
    public static Texture getGeneratorTexture(String type) {
        return switch (type) {
            case "coal" -> generatorCoal;
            case "iron" -> generatorIron;
            case "gold" -> generatorGold;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    /**
     * Update the fonts of the game.
     */
    public static void updateFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("gameui2/defaultFont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;
        parameter.color = Color.WHITE;
        FreeTypeFontGenerator.FreeTypeFontParameter parameterBig = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameterBig.size = 40;
        parameterBig.color = Color.WHITE;
        BitmapFont font = generator.generateFont(parameter);
        BitmapFont fontForLabels = generator.generateFont(parameter);
        fontForLabels.getData().setScale(2f); // Set the scale factor according to your desired size
        BitmapFont fontForNames = generator.generateFont(parameterBig);
        BitmapFont fontForMessages = generator.generateFont(parameter);

        generalFont = font;
        generalFontForLabels = fontForLabels;
        generalFontForMessages = fontForMessages;
        generator.dispose();

        labelStyle = new Label.LabelStyle();
        labelStyle.font = generalFont;

        labelStyleForLabels = new Label.LabelStyle();
        labelStyleForLabels.font = fontForNames;

        nameStyle = new Label.LabelStyle();
        generalFontForLabels.getData().setScale(4f);
        nameStyle.font = generalFontForLabels;

        skin = new Skin();
        skin.add("defaultFont", generalFont);
        skin.addRegions(new TextureAtlas(Gdx.files.internal("gameui2/ui.atlas")));
        skin.load(Gdx.files.internal("gameui2/ui.json"));

        Pixmap bgPixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(0f, 0f, 0f, 0.66f);
        bgPixmap.fill();
        generalBackground = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));

        Enemy.updateFonts();
        Turret.updateFontAndTexture();
    }

    /**
     * Get the skin of the game.
     * @return The skin of the game
     */
    public static Skin getSkin() {
        return skin;
    }

    /**
     * Get the label style of the game.
     * @return The label style of the game
     */
    public static Label.LabelStyle getLabelStyle() {
        return labelStyle;
    }

    /**
     * Get the label style for labels.
     * @return The label style for labels
     */
    public static Label.LabelStyle getLabelStyleForLabels() {
        return labelStyleForLabels;
    }

    /**
     * Get the name style of the game.
     * @return The name style of the game
     */
    public static Label.LabelStyle getNameStyle() {
        return nameStyle;
    }

    /**
     * Get the texture of the generator.
     * @return The texture of the generator
     */
    public static BitmapFont getGeneralFont() {
        return generalFont;
    }

    /**
     * Get general font for labels.
     * @return The general font for labels
     */
    public static BitmapFont getGeneralFontForLabels() {
        return generalFontForLabels;
    }

    /**
     * Get general font for messages.
     * @return The general font for messages
     */
    public static BitmapFont getGeneralFontForMessages() {
        return generalFontForMessages;
    }

    /**
     * Get the general background.
     * @return The general background
     */
    public static TextureRegionDrawable getGeneralBackground() {
        return generalBackground;
    }

}
