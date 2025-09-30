package me.vesder.blazeyChat.commands.subcommands;

import me.vesder.blazeyChat.commands.SubCommand;
import me.vesder.blazeyChat.configs.ConfigManager;
import me.vesder.blazeyChat.configs.customconfigs.SettingsConfig;
import me.vesder.blazeyChat.data.User;
import me.vesder.blazeyChat.data.UserManager;
import me.vesder.blazeyChat.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

import static me.vesder.blazeyChat.commands.CommandManager.sendHelpMessage;

public class MsgCommand implements SubCommand {

    SettingsConfig settingsConfig = (SettingsConfig) ConfigManager.getConfigManager().getCustomConfig("settings.yml");

    @Override
    public String getName() {
        return "msg";
    }

    @Override
    public String getDescription() {
        return "Send a private message to another player on the server.";
    }

    @Override
    public String getSyntax() {
        return "/bc msg <player>";
    }

    @Override
    public String getPermission() {
        return "blazeychat.command.msg";
    }

    @Override
    public void perform(Player player, String[] args) {

        if (args.length > 1) {

            Player receiver = Bukkit.getPlayer(args[1]);

            if (receiver == null) {
                player.sendMessage(Utils.buildFormattedComponent(settingsConfig.getPvMessagesNotFoundError(), player, null, null, null));
                return;
            }

            if (player.equals(receiver)) {
                player.sendMessage(Utils.buildFormattedComponent(settingsConfig.getPvMessagesSelfMsgError(), player, null, null, null));
                return;
            }

            if (args.length == 2) {
                player.sendMessage(Utils.buildFormattedComponent(settingsConfig.getPvMessagesNoMsgError(), player, null, null, null));
                return;
            }

            String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

            User senderUser = UserManager.getUser(player.getUniqueId());
            for (String action : settingsConfig.getPvMessagesSenderActions()) {
                Utils.runActionDispatcher(action, player, player, receiver, message, null);
            }
            senderUser.setLastMsgSender(receiver.getUniqueId());

            User receiverUser = UserManager.getUser(receiver.getUniqueId());
            if (receiverUser.getIgnoredPlayers() == null || !receiverUser.getIgnoredPlayers().contains(player.getUniqueId())) {
                for (String action : settingsConfig.getPvMessagesReceiverActions()) {
                    Utils.runActionDispatcher(action, receiver, player, receiver, message, null);
                }
                receiverUser.setLastMsgSender(player.getUniqueId());
            }

            for (Player spyPlayer : UserManager.getChatSpyPlayers()) {

                if (spyPlayer.equals(player) || spyPlayer.equals(receiver)) {
                    continue;
                }

                for (String action : settingsConfig.getPvMessagesReceiverActions()) {
                    Utils.runActionDispatcher(action, spyPlayer, player, receiver, message, null);
                }
            }
            return;
        }

        sendHelpMessage(player, getName());
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {

        if (args.length == 2) {
            return null;
        }

        return List.of();
    }
}
