package me.vesder.blazeyChat.commands.subcommands;

import me.vesder.blazeyChat.commands.SubCommand;
import me.vesder.blazeyChat.configs.ConfigManager;
import me.vesder.blazeyChat.configs.customconfigs.SettingsConfig;
import me.vesder.blazeyChat.data.User;
import me.vesder.blazeyChat.data.UserManager;
import me.vesder.blazeyChat.utils.TextUtils;
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
