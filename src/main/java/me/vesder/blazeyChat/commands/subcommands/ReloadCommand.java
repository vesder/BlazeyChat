package me.vesder.blazeyChat.commands.subcommands;

import me.vesder.blazeyChat.commands.SubCommand;
import me.vesder.blazeyChat.configs.ConfigManager;
import me.vesder.blazeyChat.configs.customconfigs.SettingsConfig;
import me.vesder.blazeyChat.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static me.vesder.blazeyChat.commands.CommandManager.sendHelpMessage;

public class ReloadCommand implements SubCommand {

    SettingsConfig settingsConfig = (SettingsConfig) ConfigManager.getConfigManager().getCustomConfig("settings.yml");

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
        return "/bc reload [config]";
    }

    @Override
    public String getPermission() {
        return "blazeychat.command.reload";
    }

    @Override
    public boolean allowConsole() {
        return true;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

        if (args.length < 2) {

            ConfigManager.getConfigManager().load();
            for (String action : settingsConfig.getReloadAllActions()) {
                Utils.runActionDispatcher(action, sender, sender instanceof Player ? (Player) sender : null, null, null, null);
            }

            return;
        }

        if (ConfigManager.getConfigManager().getCustomConfigNames().contains(args[1])) {
            ConfigManager.getConfigManager().load(args[1]);
            for (String action : settingsConfig.getReloadSpecificActions()) {
                Utils.runActionDispatcher(action, sender, sender instanceof Player ? (Player) sender : null, null, null, null);
            }
            return;
        }

        sendHelpMessage(sender, getName());
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {

        if (args.length == 2) {
            return ConfigManager.getConfigManager().getCustomConfigNames();
        }

        return List.of();
    }
}
