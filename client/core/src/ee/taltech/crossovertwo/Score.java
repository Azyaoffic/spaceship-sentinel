package ee.taltech.crossovertwo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import ee.taltech.crossovertwo.utilities.GraphicVariables;

import java.util.LinkedHashMap;
import java.util.Map;

public class Score {

    private static final Score INSTANCE = new Score();

    private static Table localGameSummary;
    private static Table gameSummary;
    private static Preferences score;

    private static final Map<String, Integer> scoreMap = new LinkedHashMap<>(Map.of(
            "Deaths", 0,
            "HP healed", 0,
            "Kills", 0,
            "Hits", 0,
            "Bullets fired", 0,
            "Games played", 0
    ));

    public final Map<String, Integer> localScoreMap = new LinkedHashMap<>(Map.of(
            "Deaths", 0,
            "HP healed", 0,
            "Kills", 0,
            "Hits", 0,
            "Bullets fired", 0
    ));

    /**
     * Reset the local score.
     */
    public void resetLocalScore() {
        for (Map.Entry<String, Integer> entry : localScoreMap.entrySet()) {
            entry.setValue(0);
        }
    }

    /**
     * Get the instance of the score.
     * @return The instance of the score.
     */
    public static Score getInstance() {
        return INSTANCE;
    }

    /**
     * End the game.
     */
    public void endGame() {

        for (Map.Entry<String, Integer> entry : localScoreMap.entrySet()) {
            scoreMap.put(entry.getKey(), scoreMap.get(entry.getKey()) + entry.getValue());
        }

        scoreMap.put("Games played", scoreMap.get("Games played") + 1);

        calculateLocalSummary();
        saveScore();
        resetLocalScore();
    }

    /**
     * Calculate the local summary.
     */
    private void calculateLocalSummary() {
        Label.LabelStyle skinLabel = GraphicVariables.getLabelStyle();
        
        localGameSummary = new Table();
        localGameSummary.padBottom(20);
        Table summaryNames = new Table();
        Table summaryValues = new Table();
        localGameSummary.add(summaryNames).padRight(30).left();
        localGameSummary.add(summaryValues).padLeft(30).right();

        for (Map.Entry<String, Integer> entry : localScoreMap.entrySet()) {
            Label nameLabel = new Label(entry.getKey() + ":", skinLabel);
            summaryNames.add(nameLabel).left();
            Label valueLabel = new Label(String.valueOf(entry.getValue()), skinLabel);
            summaryValues.add(valueLabel).right();
            summaryNames.row();
            summaryValues.row();
        }

        summaryNames.add(new Label("K/D:", skinLabel)).left();
        summaryValues.add(new Label(String.valueOf(Math.round((float) localScoreMap.get("Kills") / Math.max(localScoreMap.get("Deaths"), 1) * 100) / 100), skinLabel)).right();
        summaryNames.row();
        summaryValues.row();
        summaryNames.add(new Label("Accuracy (%):", skinLabel)).left();
        summaryValues.add(new Label(String.valueOf(Math.round((float) localScoreMap.get("Hits") / Math.max(localScoreMap.get("Bullets fired"), 1) * 100)), skinLabel)).right();
    }

    /**
     * Calculate the global summary.
     */
    public static void calculateGlobalSummary() {
        Label.LabelStyle skinLabel = GraphicVariables.getLabelStyle();
        Label.LabelStyle skinLabelTitle = GraphicVariables.getLabelStyleForLabels();

        gameSummary = new Table();
        gameSummary.defaults().pad(10);
        Table summaryNames = new Table();
        Table summaryValues = new Table();
        gameSummary.add(summaryNames).padRight(40).left().padBottom(40);
        gameSummary.add(summaryValues).padLeft(40).right().padBottom(40).row();
        summaryNames.add(new Label("K/D Ratio", skinLabelTitle)).left().row();
        summaryNames.add(new Label(String.valueOf(Math.round((float) scoreMap.get("Kills") / Math.max(scoreMap.get("Deaths"), 1) * 100f) / 100f), skinLabelTitle)).left().row();
        summaryValues.add(new Label("Accuracy", skinLabelTitle)).right().row();
        summaryValues.add(new Label(Math.round((float) scoreMap.get("Hits") / Math.max(scoreMap.get("Bullets fired"), 1) * 100) + "%", skinLabelTitle)).right().row();

        for (Map.Entry<String, Integer> entry : scoreMap.entrySet()) {
            Label nameLabel = new Label(entry.getKey() + ":", skinLabel);
            gameSummary.add(nameLabel).left();
            Label valueLabel = new Label(String.valueOf(entry.getValue()), skinLabel);
            gameSummary.add(valueLabel).right();
            gameSummary.row();
        }
    }

    /**
     * Initialize the preferences
     */
    public static void initialize() {
        score = Gdx.app.getPreferences("MyGameScore");
        loadScore();
        calculateGlobalSummary();
    }

    /**
     * Save a score
     */
    public static void saveScore() {
        for (Map.Entry<String, Integer> entry : scoreMap.entrySet()) {
            score.putInteger(entry.getKey(), entry.getValue());
        }
        score.flush();
    }

    /**
     * Load a score
     */
    public static void loadScore() {
        for (Map.Entry<String, Integer> entry : scoreMap.entrySet()) {
            entry.setValue(score.getInteger(entry.getKey(), 0));
        }
    }

    /**
     * Add a death
     */
    public void addDeath() {
        localScoreMap.put("Deaths", localScoreMap.get("Deaths") + 1);
    }

    /**
     * Add HP healed
     * @param hp The HP to heal
     */
    public void addHpHealed(int hp) {
        localScoreMap.put("HP healed", localScoreMap.get("HP healed") + hp);
    }

    /**
     * Add a kill
     */
    public void addKill() {
        localScoreMap.put("Kills", localScoreMap.get("Kills") + 1);
    }

    /**
     * Add a hit
     */
    public void addHit() {
        localScoreMap.put("Hits", localScoreMap.get("Hits") + 1);
    }

    /**
     * Add a bullet fired
     */
    public void addBulletFired() {
        localScoreMap.put("Bullets fired", localScoreMap.get("Bullets fired") + 1);
    }

    /**
     * Get the local game summary
     * @return The local game summary
     */
    public static Table getLocalGameSummary() {
        return localGameSummary;
    }

    /**
     * Get the game summary
     * @return The game summary
     */
    public static Table getGameSummary() {
        calculateGlobalSummary();
        return gameSummary;
    }
}
