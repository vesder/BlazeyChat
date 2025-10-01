package me.vesder.blazeyChat;

import lombok.Getter;
import me.vesder.blazeyChat.commands.CommandManager;
import me.vesder.blazeyChat.configs.ConfigManager;
import me.vesder.blazeyChat.configs.customconfigs.FilterConfig;
import me.vesder.blazeyChat.configs.customconfigs.FormatConfig;
import me.vesder.blazeyChat.configs.customconfigs.SettingsConfig;
import me.vesder.blazeyChat.hooks.MetricsLite;
import me.vesder.blazeyChat.listeners.ChatListener;
import me.vesder.blazeyChat.listeners.JoinListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static org.bukkit.Bukkit.getPluginManager;

public final class BlazeyChat extends JavaPlugin {

    @Getter
    private static BlazeyChat plugin;

    // Configs
    private final SettingsConfig settingsConfig = (SettingsConfig) ConfigManager.getConfigManager().getCustomConfig("settings.yml");
    private final FormatConfig formatConfig = (FormatConfig) ConfigManager.getConfigManager().getCustomConfig("format.yml");
    private final FilterConfig filterConfig = (FilterConfig) ConfigManager.getConfigManager().getCustomConfig("filter.yml");

    @Override
    public void onEnable() {

        // Register the plugin instance
        plugin = this;

        ConfigManager.getConfigManager().load();

        getPluginManager().registerEvents(new ChatListener(settingsConfig, formatConfig, filterConfig), this);
        getPluginManager().registerEvents(new JoinListener(settingsConfig), this);

        Objects.requireNonNull(getCommand("blazeychat")).setExecutor(new CommandManager(settingsConfig));
        Objects.requireNonNull(getCommand("shout")).setExecutor(new CommandManager(settingsConfig));
        Objects.requireNonNull(getCommand("chatspy")).setExecutor(new CommandManager(settingsConfig));
        Objects.requireNonNull(getCommand("msg")).setExecutor(new CommandManager(settingsConfig));
        Objects.requireNonNull(getCommand("reply")).setExecutor(new CommandManager(settingsConfig));
        Objects.requireNonNull(getCommand("ignore")).setExecutor(new CommandManager(settingsConfig));
//        Objects.requireNonNull(getCommand("ads")).setExecutor(new CommandManager());

        int pluginId = 27414;
        MetricsLite metricsLite = new MetricsLite(this, pluginId);

        getLogger().info("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= ★");
        getLogger().info("      BlazeyChat  ");
        getLogger().info(""); // Blank line for readability
        getLogger().info("      V:" + getDescription().getVersion());
        getLogger().info("      Made By @Vesder      ");
        getLogger().info("Contact Me In Discord For Support");
        getLogger().info("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= ★");

    }

}
