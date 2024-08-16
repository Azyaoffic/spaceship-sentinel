package ee.taltech.crossovertwo.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PreferencesSaver {
    private static Preferences preferences;

    /**
     * Initialize the preferences
     */
    public static void initialize() {
        preferences = Gdx.app.getPreferences("MyGamePreferences");
    }

    /**
     * Check if the preferences are empty
     * @return True if the preferences are empty, false otherwise
     */
    public static boolean isPreferencesEmpty() {
        System.out.println(preferences.get().size() + " " + ControlKeys.values().length);
        return preferences.get().size() < ControlKeys.values().length;
    }

    /**
     * Save a single preference
     * @param action The action to save
     * @param key The key to save
     */
    public static void savePreferences(ControlKeys action, int key) {
        preferences.putInteger(String.valueOf(action), key);
        preferences.flush();
    }

    /**
     * Save all preferences
     * @param keyMap The map of preferences to save
     */
    public static void saveAllPreferences(Map<ControlKeys, Integer> keyMap) {
        for (Map.Entry<ControlKeys, Integer> entry : keyMap.entrySet()) {
            preferences.putInteger(String.valueOf(entry.getKey()), entry.getValue());
        }
        preferences.flush();
    }

    /**
     * Load the OpenAI key
     * @param key The OpenAI key
     */
    public static void saveOpenAIKey(String key) {
        preferences.putString("openAIKey", key);
        preferences.flush();
    }

    /**
     * Load the OpenAI key
     * @return The OpenAI key
     */
    public static String loadOpenAIKey() {
        return preferences.getString("openAIKey");
    }

    /**
     * Load all preferences
     * @return The map of preferences
     */
    public static Map<ControlKeys, Integer> loadAllPreferences() {
        Map<ControlKeys, Integer> keyMap = new HashMap<>();
        List<ControlKeys> keys = List.of(ControlKeys.values());
        keys.forEach(key -> keyMap.put(key, loadPreferences(key)));
        return keyMap;
    }

    /**
     * Load a single preference
     * @param action The action to load
     * @return The key of the action
     */
    public static int loadPreferences(ControlKeys action) {
        return preferences.getInteger(String.valueOf(action), 0);
    }
}
