package me.vesder.chatCoreV.configs;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class ConfigUtils {

    private static final ConfigManager configManager = ConfigManager.getConfigManager();

    public static void set(String configName, String path, Object value) {

        configManager.getCustomConfig(configName).config.set(path, value);

        configManager.save(configName);
    }

    public static String getStringConfig(String configName,String path) {

        return configManager.getCustomConfig(configName).config.getString(path);
    }

    public static List<String> getStringListConfig(String configName,String path) {

        return configManager.getCustomConfig(configName).config.getStringList(path);

    }

    public static ConfigurationSection getConfigSection(String configName,String path) {

        return configManager.getCustomConfig(configName).config.getConfigurationSection(path);

    }

}
