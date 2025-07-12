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
    public String getPermission() {
        return "chatcorev.command.help";
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

        sender.sendMessage(miniMessage.deserialize(getDefaultHelpMessage(sender)));

    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {

        if (args.length == 2) {
            return getSubCommandNames(sender, getName());
        }

        return List.of();
    }

    private static final String HEADER = "<gradient:#55FFFF:#FFD700>==========</gradient> <light_purple><bold>ChatCoreV</bold></light_purple> <gradient:#FFD700:#55FFFF>==========</gradient>\n \n";
    private static final String FOOTER = "<gradient:#55FFFF:#FFD700>==========</gradient><gold>============</gold><gradient:#FFD700:#55FFFF>==========</gradient>";

    private String getDefaultHelpMessage(CommandSender sender) {

        StringBuilder helpMessageBuilder = new StringBuilder();
        helpMessageBuilder.append(HEADER);

        for (SubCommand subCommand : getSubCommands()) {

            if (!sender.hasPermission(subCommand.getPermission())) {
                continue;
            }

            helpMessageBuilder.append(String.format("""
                <click:suggest_command:'%1$s'><hover:show_text:'<aqua>%1$s</aqua><br><gray>%2$s</gray><br><yellow>Tip: Click on the commands.</yellow>'>%1$s</hover></click>
                <gray>%2$s</gray>
                
                """, subCommand.getSyntax(), subCommand.getDescription()));
        }

        helpMessageBuilder.append(FOOTER);
        return parseLegacyColorCodes(helpMessageBuilder.toString());
    }

    private String getHelpMessage(CommandSender sender, String commandName) {

        StringBuilder helpMessageBuilder = new StringBuilder();
        helpMessageBuilder.append(HEADER);

        for (String subArg : getSubCommand(commandName).getSubcommandArguments(sender, new String[]{commandName, ""})) {
            helpMessageBuilder.append("/ccv ").append(commandName).append(" ").append(subArg)
                .append("\n \n");
        }

        helpMessageBuilder.append(FOOTER);
        return parseLegacyColorCodes(helpMessageBuilder.toString());
    }

}
