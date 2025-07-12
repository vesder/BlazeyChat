package me.vesder.chatCoreV.commands.subcommands;

import me.vesder.chatCoreV.commands.SubCommand;
import me.vesder.chatCoreV.configs.ConfigUtils;
import me.vesder.chatCoreV.data.User;
import me.vesder.chatCoreV.data.UserManager;
import me.vesder.chatCoreV.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ShoutCommand implements SubCommand {

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
    public void perform(Player player, String[] args) {

        Component toggleEnableMessage = MiniMessage.miniMessage().deserialize(TextUtils
            .parseLegacyColorCodes(ConfigUtils.getStringConfig("settings.yml", "shout.actions.toggle.enable.message")));
        Component toggleDisableMessage = MiniMessage.miniMessage().deserialize(TextUtils
            .parseLegacyColorCodes(ConfigUtils.getStringConfig("settings.yml", "shout.actions.toggle.disable.message")));

        User user = UserManager.getUser(player.getUniqueId());
        user.setShout(!user.isShout());
        player.sendMessage(user.isShout() ? toggleEnableMessage : toggleDisableMessage);

    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return List.of();
    }
}
