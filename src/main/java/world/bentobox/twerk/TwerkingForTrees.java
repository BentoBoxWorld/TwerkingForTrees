package world.bentobox.twerk;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.twerk.listeners.TreeGrowListener;

public final class TwerkingForTrees extends Addon {

    // Settings
    private Settings settings;
    private Config<Settings> configObject = new Config<>(this, Settings.class);

    @Override
    public void onLoad() {
        // Save the default config from config.yml
        saveDefaultConfig();
        // Load the config
        loadSettings();
    }

    /**
     * @since 1.1.0
     */
    private void loadSettings() {
        // Load settings
        settings = configObject.loadConfigObject();
        // Save config
        configObject.saveConfigObject(settings);
    }

    @Override
    public void onEnable() {
        // Register listener
        registerListener(new TreeGrowListener(this));
    }

    @Override
    public void onDisable() {
        // Nothing to do here
    }

    @Override
    public void onReload() {
        // Reload the config
        loadSettings();
    }

    /**
     * Returns the Settings instance
     * @return the settings
     * @since 1.1.0
     */
    public Settings getSettings() {
        return settings;
    }
}
