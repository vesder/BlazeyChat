package me.vesder.blazeyChat.commands.subcommands;

import me.vesder.blazeyChat.commands.SubCommand;
import me.vesder.blazeyChat.configs.customconfigs.SettingsConfig;
import me.vesder.blazeyChat.data.User;
import me.vesder.blazeyChat.data.UserManager;
import me.vesder.blazeyChat.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatSpyCommand implements SubCommand {

    private final SettingsConfig settingsConfig;

    public ChatSpyCommand(SettingsConfig settingsConfig) {
        this.settingsConfig = settingsConfig;
    }

    @Override
    public String getName() {
        return "chatspy";
    }

    @Override
    public String getDescription() {
        return "Toggle chat spy mode to see other players messages.";
    }

    @Override
    public String getSyntax() {
        return "/bc chatspy";
    }

    @Override
    public String getPermission() {
        return "blazeychat.command.chatspy";
    }

    @Override
    public void perform(Player player, String[] args) {

        User user = UserManager.getUser(player.getUniqueId());
        user.setChatSpy(!user.isChatSpy());
        for (String action : user.isChatSpy() ? settingsConfig.getChatspyEnableActions() : settingsConfig.getChatspyDisableActions()) {
            Utils.runActionDispatcher(action, player, player, null, null, null);
        }

    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return List.of();
    }
}
