package me.vesder.blazeyChat;

import lombok.Getter;
import me.vesder.blazeyChat.commands.CommandManager;
import me.vesder.blazeyChat.configs.ConfigManager;
import me.vesder.blazeyChat.configs.customconfigs.FilterConfig;
import me.vesder.blazeyChat.configs.customconfigs.FormatConfig;
import me.vesder.blazeyChat.configs.customconfigs.SettingsConfig;
import me.vesder.blazeyChat.database.User;
import me.vesder.blazeyChat.database.UserDatabase;
import me.vesder.blazeyChat.database.UserManager;
import me.vesder.blazeyChat.hooks.MetricsLite;
import me.vesder.blazeyChat.hooks.UpdateChecker;
import me.vesder.blazeyChat.listeners.ChatListener;
import me.vesder.blazeyChat.listeners.JoinListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getPluginManager;

public final class BlazeyChat extends JavaPlugin {

    @Getter
    private static BlazeyChat plugin;

    // Configs
    private final SettingsConfig settingsConfig =
        (SettingsConfig) ConfigManager.getConfigManager().getCustomConfig("settings.yml");
    private final FormatConfig formatConfig =
        (FormatConfig) ConfigManager.getConfigManager().getCustomConfig("format.yml");
    private final FilterConfig filterConfig =
        (FilterConfig) ConfigManager.getConfigManager().getCustomConfig("filter.yml");

    @Override
    public void onEnable() {

        // Register the plugin instance
        plugin = this;

        // Load configs
        ConfigManager.getConfigManager().load();

        // Register events listeners
        getPluginManager().registerEvents(new ChatListener(settingsConfig, formatConfig, filterConfig), this);
        getPluginManager().registerEvents(new JoinListener(settingsConfig), this);

        // Register commands
        Objects.requireNonNull(getCommand("blazeychat")).setExecutor(new CommandManager(settingsConfig));
        Objects.requireNonNull(getCommand("shout")).setExecutor(new CommandManager(settingsConfig));
        Objects.requireNonNull(getCommand("chatspy")).setExecutor(new CommandManager(settingsConfig));
        Objects.requireNonNull(getCommand("msg")).setExecutor(new CommandManager(settingsConfig));
        Objects.requireNonNull(getCommand("reply")).setExecutor(new CommandManager(settingsConfig));
        Objects.requireNonNull(getCommand("ignore")).setExecutor(new CommandManager(settingsConfig));

        // Setup metrics with bStats
        int pluginId = 27414;
        MetricsLite metricsLite = new MetricsLite(this, pluginId);

        // Log plugin info to console
        getLogger().info("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= ★");
        getLogger().info("      BlazeyChat  ");
        getLogger().info(""); // Blank line for readability
        getLogger().info("      V:" + getDescription().getVersion());
        getLogger().info("      Made By @Vesder      ");
        getLogger().info("Contact Me In Discord For Support");
        getLogger().info("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= ★");

        // Check for plugin updates
        new UpdateChecker(this, 129211).getVersion(version -> {
            if (!getDescription().getVersion().equals(version)) {
                getLogger().warning(
                    "A new version is available! (Current: " + getDescription().getVersion() + ", Latest: " + version + ")"
                );
            }
        });

    }

    @Override
    public void onDisable() {

        for (Map.Entry<UUID, User> entry : UserManager.userMap.entrySet()) {
            try {
                UserDatabase.getInstance().saveUserData(entry.getValue(), entry.getKey());
            } catch (SQLException ex) {
                getLogger().log(Level.WARNING, "Failed to store player data in the database!", ex);
            }
        }

        try {
            UserDatabase.getInstance().closeConnection();
        } catch (SQLException ex) {
            getLogger().log(Level.WARNING, "Failed to close database connection!", ex);
        }

    }
}
