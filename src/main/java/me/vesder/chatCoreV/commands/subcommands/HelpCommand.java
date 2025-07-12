package me.vesder.chatCoreV.commands.subcommands;

import me.vesder.chatCoreV.commands.SubCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.util.List;

import static me.vesder.chatCoreV.commands.CommandManager.getSubCommand;
import static me.vesder.chatCoreV.commands.CommandManager.getSubCommandNames;
import static me.vesder.chatCoreV.commands.CommandManager.getSubCommands;
import static me.vesder.chatCoreV.utils.TextUtils.parseLegacyColorCodes;

public class HelpCommand implements SubCommand {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Shows help information for commands";
    }

    @Override
    public String getSyntax() {
        return "/ccv help [command]";
    }

    @Override
    public boolean allowConsole() {
        return true;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

        MiniMessage miniMessage = MiniMessage.miniMessage();

        if (args.length >= 2 && getSubCommandNames(sender).contains(args[1].toLowerCase())) {
            sender.sendMessage(miniMessage.deserialize(getHelpMessage(sender, args[1].toLowerCase())));
            return;
        }

        sender.sendMessage(miniMessage.deserialize(getDefaultHelpMessage()));

    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {

        if (args.length == 2) {
            return getSubCommandNames(sender, getName());
        }

        return List.of();
    }

//    private final Map<String, String> cachedHelpMessages = new HashMap<>(); // name -> message

    private static final String HEADER = "<gradient:#55FFFF:#FFD700>==========</gradient> <light_purple><bold>ChatCoreV</bold></light_purple> <gradient:#FFD700:#55FFFF>==========</gradient>\n \n";
    private static final String FOOTER = "<gradient:#55FFFF:#FFD700>==========</gradient><gold>============</gold><gradient:#FFD700:#55FFFF>==========</gradient>";

    private String getDefaultHelpMessage() {

//        String commandName = "help";

//        if (cachedHelpMessages.containsKey(commandName)) {
//            return cachedHelpMessages.get(commandName);
//        }

        StringBuilder helpMessageBuilder = new StringBuilder();
        helpMessageBuilder.append(HEADER);

        for (SubCommand subCommand : getSubCommands()) {
            helpMessageBuilder.append(subCommand.getSyntax())
                .append("\n")
                .append(subCommand.getDescription())
                .append("\n \n");
        }

        helpMessageBuilder.append(FOOTER);
//        cachedHelpMessages.put(commandName, parseLegacyColorCodes(helpMessageBuilder.toString()));
//
//        return cachedHelpMessages.get(commandName);
        return parseLegacyColorCodes(helpMessageBuilder.toString());
    }

    private String getHelpMessage(CommandSender sender, String commandName) {

//        if (cachedHelpMessages.containsKey(commandName)) {
//            return cachedHelpMessages.get(commandName);
//        }

        StringBuilder helpMessageBuilder = new StringBuilder();
        helpMessageBuilder.append(HEADER);

        for (String subArg : getSubCommand(commandName).getSubcommandArguments(sender, new String[]{commandName, ""})) {
            helpMessageBuilder.append("/ccv ").append(commandName).append(" ").append(subArg)
                .append("\n \n");
        }

        helpMessageBuilder.append(FOOTER);
//        cachedHelpMessages.put(commandName, parseLegacyColorCodes(helpMessageBuilder.toString()));
//
//        return cachedHelpMessages.get(commandName);
        return parseLegacyColorCodes(helpMessageBuilder.toString());
    }

}
