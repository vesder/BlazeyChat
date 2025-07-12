package me.vesder.chatCoreV.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public interface SubCommand {

    String getName();

    String getDescription();

    String getSyntax();

    default String getPermission() {
        return null;
    }

    default boolean allowConsole() {
        return false;
    }

    default void perform(Player player, String[] args) {}

    default void perform(CommandSender sender, String[] args) {}

    List<String> getSubcommandArguments(CommandSender sender,String[] args);

}
