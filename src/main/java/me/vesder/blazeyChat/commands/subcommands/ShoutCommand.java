package me.vesder.blazeyChat.commands.subcommands;

import me.vesder.blazeyChat.commands.SubCommand;
import me.vesder.blazeyChat.configs.customconfigs.SettingsConfig;
import me.vesder.blazeyChat.data.User;
import me.vesder.blazeyChat.data.UserManager;
import me.vesder.blazeyChat.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ShoutCommand implements SubCommand {

    private final SettingsConfig settingsConfig;

    public ShoutCommand(SettingsConfig settingsConfig) {
        this.settingsConfig = settingsConfig;
    }

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
        return "/bc shout";
    }

    @Override
    public String getPermission() {
        return "blazeychat.command.shout";
    }

    @Override
    public void perform(Player player, String[] args) {

        User user = UserManager.getUser(player.getUniqueId());
        user.setShout(!user.isShout());
        for (String action : user.isShout() ? settingsConfig.getShoutEnableActions() : settingsConfig.getShoutDisableActions()) {
            Utils.runActionDispatcher(action, player, player, null, null, null);
        }

    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return List.of();
    }
}
