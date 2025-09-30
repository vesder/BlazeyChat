package me.vesder.blazeyChat;

import lombok.Getter;
import me.vesder.blazeyChat.commands.CommandManager;
import me.vesder.blazeyChat.configs.ConfigManager;
import me.vesder.blazeyChat.hooks.MetricsLite;
import me.vesder.blazeyChat.listeners.ChatListener;
import me.vesder.blazeyChat.listeners.JoinListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static org.bukkit.Bukkit.getPluginManager;

public final class BlazeyChat extends JavaPlugin {

    @Getter
    private static BlazeyChat plugin;

    @Override
    public void onEnable() {

        // Register the plugin instance
        plugin = this;

        getPluginManager().registerEvents(new ChatListener(), this);
        getPluginManager().registerEvents(new JoinListener(), this);

        Objects.requireNonNull(getCommand("blazeychat")).setExecutor(new CommandManager());
        Objects.requireNonNull(getCommand("shout")).setExecutor(new CommandManager());
        Objects.requireNonNull(getCommand("chatspy")).setExecutor(new CommandManager());
        Objects.requireNonNull(getCommand("msg")).setExecutor(new CommandManager());
        Objects.requireNonNull(getCommand("reply")).setExecutor(new CommandManager());
        Objects.requireNonNull(getCommand("ignore")).setExecutor(new CommandManager());
//        Objects.requireNonNull(getCommand("ads")).setExecutor(new CommandManager());

        ConfigManager.getConfigManager().load();

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
