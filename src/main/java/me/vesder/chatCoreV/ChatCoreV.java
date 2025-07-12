package me.vesder.chatCoreV;

import lombok.Getter;
import me.vesder.chatCoreV.commands.CommandManager;
import me.vesder.chatCoreV.configs.ConfigManager;
import me.vesder.chatCoreV.listeners.ChatListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static org.bukkit.Bukkit.getPluginManager;

public final class ChatCoreV extends JavaPlugin {

    @Getter
    private static ChatCoreV plugin;

    @Override
    public void onEnable() {

        // Register the plugin instance
        plugin = this;

        getPluginManager().registerEvents(new ChatListener(),this);

        Objects.requireNonNull(getCommand("chatcorev")).setExecutor(new CommandManager());

        ConfigManager.getConfigManager().load();

        getLogger().info("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= ★");
        getLogger().info("      ChatCoreV  ");
        getLogger().info(""); // Blank line for readability
        getLogger().info("      V:" + getPluginMeta().getVersion());
        getLogger().info("      Made By @Vesder      ");
        getLogger().info("Contact Me In Discord For Support");
        getLogger().info("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= ★");

    }

}
