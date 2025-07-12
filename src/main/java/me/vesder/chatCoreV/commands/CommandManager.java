package me.vesder.chatCoreV.commands;

import me.vesder.chatCoreV.commands.subcommands.AdsCommand;
import me.vesder.chatCoreV.commands.subcommands.ChatSpyCommand;
import me.vesder.chatCoreV.commands.subcommands.HelpCommand;
import me.vesder.chatCoreV.commands.subcommands.ReloadCommand;
import me.vesder.chatCoreV.commands.subcommands.ShoutCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager implements TabExecutor {

    private static final Map<String, SubCommand> subCommandMap = new HashMap<>();

    static {

        registerSubCommands(

            new HelpCommand(),
            new ReloadCommand(),
            new ShoutCommand(),
            new ChatSpyCommand(),
            new AdsCommand()

        );

    }

    private static void registerSubCommands(SubCommand... subCommands) {

        for (SubCommand subCommand : subCommands) {
            subCommandMap.put(subCommand.getName().toLowerCase(), subCommand);
        }

    }

    public static SubCommand getSubCommand(String name) {
        return subCommandMap.get(name.toLowerCase());
    }

    public static Collection<SubCommand> getSubCommands() {
        return subCommandMap.values();
    }

    // make better HelpMessage system later
    public static void sendHelpMessage(Player player, String name) {
        getSubCommand("help").perform(player, new String[]{"help", name});
    }

    /**
     * Returns a list of all subcommand names, excluding any names provided.
     */
    public static List<String> getSubCommandNames(CommandSender sender, String... excludedNames) {

        List<String> filteredNames = new ArrayList<>(subCommandMap.keySet());

        for (String excludedName : excludedNames) {
            filteredNames.remove(excludedName.toLowerCase());
        }

        filteredNames.removeIf(filteredName -> {
            SubCommand subCommand = getSubCommand(filteredName);
            return subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission());
        });

        return filteredNames;
    }

    public static List<String> getSubCommandNames(String... excludedNames) {

        List<String> filteredNames = new ArrayList<>(subCommandMap.keySet());

        for (String excludedName : excludedNames) {
            filteredNames.remove(excludedName.toLowerCase());
        }

        return filteredNames;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length >= 1 && getSubCommandNames(sender).contains(args[0].toLowerCase())) {

            SubCommand subCommand = getSubCommand(args[0]);

            if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
                return true;
            }

            if (subCommand.allowConsole()) {
                subCommand.perform(sender, args);
                return true;
            }

            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command");
                return true;
            }

            subCommand.perform(player, args);
            return true;
        }

        getSubCommand("help").perform(sender, args);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            return getSubCommandNames(sender);
        }

        if (args.length >= 2 && getSubCommandNames(sender).contains(args[0].toLowerCase())) {
            return getSubCommand(args[0]).getSubcommandArguments(sender, args);
        }

        return List.of();
    }

}
