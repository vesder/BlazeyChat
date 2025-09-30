package me.vesder.blazeyChat.configs;

import me.vesder.blazeyChat.BlazeyChat;
import me.vesder.blazeyChat.configs.customconfigs.FilterConfig;
import me.vesder.blazeyChat.configs.customconfigs.SettingsConfig;
import me.vesder.blazeyChat.configs.customconfigs.FormatConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ConfigManager {

    // Static fields

    private static final ConfigManager instance = new ConfigManager();
    private static final Map<String, CustomConfig> customConfigMap = new HashMap<>();

    static {

        registerCustomConfigs(

            new SettingsConfig(),
            new FormatConfig(),
            new FilterConfig()

        );

    }

    private ConfigManager() {}

    // Public methods

    public static ConfigManager getConfigManager() {
        return instance;
    }

    public Collection<CustomConfig> getCustomConfigs() {
        return customConfigMap.values();
    }

    public List<String> getCustomConfigNames() {

        return new ArrayList<>(customConfigMap.keySet());
    }

    public CustomConfig getCustomConfig(String name) {
        return customConfigMap.get(name.toLowerCase());
    }

    public void load() {

        for (CustomConfig customConfig : getCustomConfigs()) {
            customConfig.file = new File(BlazeyChat.getPlugin().getDataFolder(), customConfig.getName());

            if (!customConfig.file.exists()) {
                BlazeyChat.getPlugin().saveResource(customConfig.getName(), false);
            }

            customConfig.config = new YamlConfiguration();
            customConfig.config.options().parseComments(true);

            try {
                customConfig.config.load(customConfig.file);
                customConfig.loadValues();
            } catch (Exception ex) {
                BlazeyChat.getPlugin().getLogger().log(Level.WARNING,
                    "Unexpected exception while loading config " + "( " + customConfig.getName() + " )", ex);
            }

        }

    }

    public void load(String configName) {

        CustomConfig customConfig = getCustomConfig(configName);

        customConfig.file = new File(BlazeyChat.getPlugin().getDataFolder(), customConfig.getName());

        if (!customConfig.file.exists()) {
            BlazeyChat.getPlugin().saveResource(customConfig.getName(), false);
        }

        customConfig.config = new YamlConfiguration();
        customConfig.config.options().parseComments(true);

        try {
            customConfig.config.load(customConfig.file);
            customConfig.loadValues();
        } catch (Exception ex) {
            BlazeyChat.getPlugin().getLogger().log(Level.WARNING,
                "Unexpected exception while loading config " + "( " + customConfig.getName() + " )", ex);
        }

    }

    public void save() {

        for (CustomConfig customConfig : getCustomConfigs()) {
            try {
                customConfig.config.save(customConfig.file);
            } catch (Exception ex) {
                BlazeyChat.getPlugin().getLogger().log(Level.WARNING,
                    "Unexpected exception while saving config " + "( " + customConfig.getName() + " )", ex);
            }
        }

    }

    public void save(String configName) {

        CustomConfig customConfig = getCustomConfig(configName);

        try {
            customConfig.config.save(customConfig.file);
        } catch (Exception ex) {
            BlazeyChat.getPlugin().getLogger().log(Level.WARNING,
                "Unexpected exception while saving config " + "( " + configName + " )", ex);
        }

    }

    // Private methods

    private static void registerCustomConfigs(CustomConfig... customConfigs) {

        for (CustomConfig customConfig : customConfigs) {
            customConfigMap.put(customConfig.getName().toLowerCase(), customConfig);
        }

    }

}
