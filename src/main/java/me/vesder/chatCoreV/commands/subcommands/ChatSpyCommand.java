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

public class ChatSpyCommand implements SubCommand {

    SettingsConfig settingsConfig = (SettingsConfig) ConfigManager.getConfigManager().getCustomConfig("settings.yml");

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
        return "/ccv chatspy";
    }

    @Override
    public String getPermission() {
        return "chatcorev.command.chatspy";
    }

    @Override
    public void perform(Player player, String[] args) {

        User user = UserManager.getUser(player.getUniqueId());
        user.setChatSpy(!user.isChatSpy());
        for (String action : user.isChatSpy() ? settingsConfig.getChatspyEnableActions() : settingsConfig.getChatspyDisableActions()) {
            TextUtils.runActionDispatcher(action, player, player, null, null, null);
        }

    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return List.of();
    }
}
