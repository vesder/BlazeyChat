package me.vesder.blazeyChat.commands;

import me.vesder.blazeyChat.commands.subcommands.ChatSpyCommand;
import me.vesder.blazeyChat.commands.subcommands.HelpCommand;
import me.vesder.blazeyChat.commands.subcommands.IgnoreCommand;
import me.vesder.blazeyChat.commands.subcommands.MsgCommand;
import me.vesder.blazeyChat.commands.subcommands.ReloadCommand;
import me.vesder.blazeyChat.commands.subcommands.ReplyCommand;
import me.vesder.blazeyChat.commands.subcommands.ShoutCommand;
import me.vesder.blazeyChat.configs.customconfigs.SettingsConfig;
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

    public CommandManager(SettingsConfig settingsConfig) {

        registerSubCommands(

            new HelpCommand(),
            new ReloadCommand(settingsConfig),
            new ShoutCommand(settingsConfig),
            new ChatSpyCommand(settingsConfig),
//            new AdsCommand(),
            new MsgCommand(settingsConfig),
            new ReplyCommand(settingsConfig),
            new IgnoreCommand(settingsConfig)

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
    public static void sendHelpMessage(CommandSender sender, String name) {
        getSubCommand("help").perform(sender, new String[]{"help", name});
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
            return !sender.hasPermission(subCommand.getPermission());
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

    private String[] prependArg(String first, String[] args) {

        String[] newArgs = new String[args.length + 1];
        newArgs[0] = first.toLowerCase();
        System.arraycopy(args, 0, newArgs, 1, args.length);

        return newArgs;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(command.getName().equalsIgnoreCase("bc") || command.getName().equalsIgnoreCase("blazeychat"))) {

            SubCommand subCommand = getSubCommand(command.getName().toLowerCase());

            String[] prependedArgs = prependArg(command.getName(), args);

            if (!sender.hasPermission(subCommand.getPermission())) {
                return true;
            }

            if (subCommand.allowConsole()) {
                subCommand.perform(sender, prependedArgs);
                return true;
            }

            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command");
                return true;
            }

            subCommand.perform(player, prependedArgs);
            return true;
        }

        if (args.length >= 1 && getSubCommandNames(sender).contains(args[0].toLowerCase())) {

            SubCommand subCommand = getSubCommand(args[0]);

            if (!sender.hasPermission(subCommand.getPermission())) {
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

        if (!(command.getName().equalsIgnoreCase("bc") || command.getName().equalsIgnoreCase("blazeychat"))) {

            String[] prependedArgs = prependArg(command.getName(), args);

            if (prependedArgs.length >= 2 && getSubCommandNames(sender).contains(prependedArgs[0].toLowerCase())) {
                return getSubCommand(prependedArgs[0]).getSubcommandArguments(sender, prependedArgs);
            }

            return List.of();
        }

        if (args.length == 1) {
            return getSubCommandNames(sender);
        }

        if (args.length >= 2 && getSubCommandNames(sender).contains(args[0].toLowerCase())) {
            return getSubCommand(args[0]).getSubcommandArguments(sender, args);
        }

        return List.of();
    }

}
