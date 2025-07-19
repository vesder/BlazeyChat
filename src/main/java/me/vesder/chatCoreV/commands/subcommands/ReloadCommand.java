package me.vesder.chatCoreV.commands.subcommands;

import me.vesder.chatCoreV.commands.SubCommand;
import me.vesder.chatCoreV.configs.ConfigManager;
import me.vesder.chatCoreV.configs.customconfigs.SettingsConfig;
import me.vesder.chatCoreV.utils.TextUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static me.vesder.chatCoreV.commands.CommandManager.sendHelpMessage;

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
        return "/ccv reload [config]";
    }

    @Override
    public String getPermission() {
        return "chatcorev.command.reload";
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
                TextUtils.runActionDispatcher(action, sender, sender instanceof Player ? (Player) sender : null, null, null, null);
            }

            return;
        }

        if (ConfigManager.getConfigManager().getCustomConfigNames().contains(args[1])) {
            ConfigManager.getConfigManager().load(args[1]);
            for (String action : settingsConfig.getReloadSpecificActions()) {
                TextUtils.runActionDispatcher(action, sender, sender instanceof Player ? (Player) sender : null, null, null, null);
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
