package me.vesder.chatCoreV.commands.subcommands;

import me.vesder.chatCoreV.commands.SubCommand;
import me.vesder.chatCoreV.configs.ConfigManager;
import me.vesder.chatCoreV.configs.CustomConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ReloadCommand implements SubCommand {

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads the plugin configuration files";
    }

    @Override
    public String getSyntax() {
        return "/ccv reload [config]";
    }

    @Override
    public String getPermission() {
        return "chatcorev.command.reload";
    }

    @Override
    public void perform(Player player, String[] args) {

        ConfigManager.getConfigManager().load();
        for (CustomConfig customConfig : ConfigManager.getConfigManager().getCustomConfigs()) {
            customConfig.loadValues();
        }

        player.sendMessage("DEBUG : All configs reloaded successfully!");
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {

        if (args.length == 2) {
            return ConfigManager.getConfigManager().getCustomConfigNames();
        }

        return List.of();
    }
}
