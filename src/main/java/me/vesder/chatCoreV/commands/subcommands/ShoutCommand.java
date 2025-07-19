package me.vesder.chatCoreV.commands.subcommands;

import me.vesder.chatCoreV.commands.SubCommand;
import me.vesder.chatCoreV.configs.ConfigManager;
import me.vesder.chatCoreV.configs.customconfigs.SettingsConfig;
import me.vesder.chatCoreV.data.User;
import me.vesder.chatCoreV.data.UserManager;
import me.vesder.chatCoreV.utils.TextUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ShoutCommand implements SubCommand {

    SettingsConfig settingsConfig = (SettingsConfig) ConfigManager.getConfigManager().getCustomConfig("settings.yml");

    @Override
    public String getName() {
        return "shout";
    }

    @Override
    public String getDescription() {
        return "Send a message to all players.";
    }

    @Override
    public String getSyntax() {
        return "/ccv shout [message/toggle] [world]";
    }

    @Override
    public String getPermission() {
        return "chatcorev.command.shout";
    }

    @Override
    public void perform(Player player, String[] args) {

        User user = UserManager.getUser(player.getUniqueId());
        user.setShout(!user.isShout());
        for (String action : user.isShout() ? settingsConfig.getShoutEnableActions() : settingsConfig.getShoutDisableActions()) {
            TextUtils.runActionDispatcher(action, player, player, null, null, null);
        }

    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return List.of();
    }
}
