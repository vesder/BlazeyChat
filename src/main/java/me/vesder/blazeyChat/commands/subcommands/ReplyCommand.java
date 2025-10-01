package me.vesder.blazeyChat.commands.subcommands;

import me.vesder.blazeyChat.commands.CommandManager;
import me.vesder.blazeyChat.commands.SubCommand;
import me.vesder.blazeyChat.configs.customconfigs.SettingsConfig;
import me.vesder.blazeyChat.data.User;
import me.vesder.blazeyChat.data.UserManager;
import me.vesder.blazeyChat.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ReplyCommand implements SubCommand {

    private final SettingsConfig settingsConfig;

    public ReplyCommand(SettingsConfig settingsConfig) {
        this.settingsConfig = settingsConfig;
    }

    @Override
    public String getName() {
        return "reply";
    }

    @Override
    public String getDescription() {
        return "Reply to your current reply target.";
    }

    @Override
    public String getSyntax() {
        return "/bc reply <message>";
    }

    @Override
    public String getPermission() {
        return "blazeychat.command.reply";
    }

    @Override
    public void perform(Player player, String[] args) {

        User user = UserManager.getUser(player.getUniqueId());
        UUID targetUUID = user.getReplyTarget();
        UUID senderUUID = user.getLastMsgSender();

        if (targetUUID != null && Bukkit.getPlayer(targetUUID) != null) {
            args[0] = Objects.requireNonNull(Bukkit.getPlayer(targetUUID)).getName();
        } else if (senderUUID != null && Bukkit.getPlayer(senderUUID) != null) {
            args[0] = Objects.requireNonNull(Bukkit.getPlayer(senderUUID)).getName();
        } else {
            player.sendMessage(Utils.buildFormattedComponent(settingsConfig.getPvMessagesNotFoundError(), player, null, null, null));
            return;
        }

        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "msg";
        System.arraycopy(args, 0, newArgs, 1, args.length);

        CommandManager.getSubCommand("msg").perform(player, newArgs);
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {

        if (args.length != 2 || !args[1].isBlank() || !(sender instanceof Player player)) {
            return List.of();
        }

        User user = UserManager.getUser(player.getUniqueId());

        if (user.getLastMsgSender() != null) {

            Player targetPlayer = Bukkit.getPlayer(user.getLastMsgSender());

            if (targetPlayer != null) {

                for (String action : settingsConfig.getReplySetActions()) {
                    Utils.runActionDispatcher(action, player, player, targetPlayer, null, null);
                }

                user.setReplyTarget(user.getLastMsgSender());
                return List.of();
            }
        }

        for (String action : settingsConfig.getReplyNotFoundActions()) {
            Utils.runActionDispatcher(action, player, player, null, null, null);
        }

        return List.of();
    }

}
