package me.vesder.chatCoreV.commands.subcommands;

import me.vesder.chatCoreV.commands.SubCommand;
import me.vesder.chatCoreV.data.User;
import me.vesder.chatCoreV.data.UserManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatSpyCommand implements SubCommand {
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
    public void perform(Player player, String[] args) {

        User user = UserManager.getUser(player.getUniqueId());
        user.setChatSpy(!user.isChatSpy());
        player.sendMessage("DEBUG : Chat spy is now " + (user.isChatSpy() ? "enabled" : "disabled") + ".");

    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return List.of();
    }
}
