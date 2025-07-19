package me.vesder.chatCoreV.commands.subcommands;

import me.vesder.chatCoreV.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class IgnoreCommand implements SubCommand {

    @Override
    public String getName() {
        return "ignore";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return "/ccv ignore <player/all>";
    }

    @Override
    public String getPermission() {
        return "chatcorev.command.ignore";
    }

    @Override
    public void perform(Player player, String[] args) {



    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return List.of();
    }
}
