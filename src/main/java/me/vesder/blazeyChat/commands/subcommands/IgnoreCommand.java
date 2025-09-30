package me.vesder.blazeyChat.commands.subcommands;

import me.vesder.blazeyChat.commands.SubCommand;
import me.vesder.blazeyChat.configs.ConfigManager;
import me.vesder.blazeyChat.configs.customconfigs.SettingsConfig;
import me.vesder.blazeyChat.data.User;
import me.vesder.blazeyChat.data.UserManager;
import me.vesder.blazeyChat.utils.TextUtils;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static me.vesder.blazeyChat.utils.TextUtils.parseLegacyColorCodes;

public class IgnoreCommand implements SubCommand {

    SettingsConfig settingsConfig = (SettingsConfig) ConfigManager.getConfigManager().getCustomConfig("settings.yml");

    private static final String HEADER = "<gradient:#00FFE0:#EB00FF>==========</gradient> <#FDD017><bold>ChatCoreV</bold></#FDD017> <gradient:#EB00FF:#00FFE0>==========</gradient>\n \n";
    private static final String FOOTER = "<gradient:#00FFE0:#EB00FF>==========</gradient><#EB00FF>============</#EB00FF><gradient:#EB00FF:#00FFE0>==========</gradient>";

    @Override
    public String getName() {
        return "ignore";
    }

    @Override
    public String getDescription() {
        return "Manage ignored players.";
    }

    @Override
    public String getSyntax() {
        return "/ccv ignore <player/#all/#clear>";
    }

    @Override
    public String getPermission() {
        return "chatcorev.command.ignore";
    }

    @Override
    public void perform(Player player, String[] args) {

        User user = UserManager.getUser(player.getUniqueId());

        if (args.length >= 2) {

            if (args[1].equalsIgnoreCase("#all")) {
                user.setIgnoreAll(!user.isIgnoreAll());
                for (String action : user.isIgnoreAll() ? settingsConfig.getIgnoreAllEnableActions() : settingsConfig.getIgnoreAllDisableActions()) {
                    TextUtils.runActionDispatcher(action, player, player, null, null, null);
                }
                if (args.length > 2 && args[2].equalsIgnoreCase("#refresh")) {
                    player.performCommand("ccv ignore");
                }
                return;
            }

            if (args[1].equalsIgnoreCase("#clear")) {
                if (user.getIgnoredPlayers() != null) {
                    user.getIgnoredPlayers().clear();
                }
                user.setIgnoreAll(false);
                for (String action : settingsConfig.getIgnoreClearActions()) {
                    TextUtils.runActionDispatcher(action, player, player, null, null, null);
                }
                if (args.length > 2 && args[2].equalsIgnoreCase("#refresh")) {
                    player.performCommand("ccv ignore");
                }
                return;
            }

            Player receiver = Bukkit.getPlayer(args[1]);

            if (receiver == null) {
                player.sendMessage(TextUtils.buildFormattedComponent(settingsConfig.getIgnoreNotFoundError(), player, null, null, null));
                return;
            }

            if (player.equals(receiver)) {
                player.sendMessage(TextUtils.buildFormattedComponent(settingsConfig.getIgnoreSelfIgnoreError(), player, receiver, null, null));
                return;
            }

            if (user.getIgnoredPlayers() == null) {

                Set<UUID> newIgnoredSet = new HashSet<>();
                newIgnoredSet.add(receiver.getUniqueId());

                user.setIgnoredPlayers(newIgnoredSet);

                for (String action : settingsConfig.getIgnoreAddActions()) {
                    TextUtils.runActionDispatcher(action, player, player, receiver, null, null);
                }

                return;
            }

            Set<UUID> ignoredPlayers = user.getIgnoredPlayers();

            if (ignoredPlayers.contains(receiver.getUniqueId())) {
                ignoredPlayers.remove(receiver.getUniqueId());
                for (String action : settingsConfig.getIgnoreRemoveActions()) {
                    TextUtils.runActionDispatcher(action, player, player, receiver, null, null);
                }
                return;
            }

            ignoredPlayers.add(receiver.getUniqueId());
            for (String action : settingsConfig.getIgnoreAddActions()) {
                TextUtils.runActionDispatcher(action, player, player, receiver, null, null);
            }

            return;
        }

        StringBuilder ignoreListBuilder = new StringBuilder();
        ignoreListBuilder.append(HEADER);

        boolean hasIgnoreList = user.getIgnoredPlayers() != null && !user.getIgnoredPlayers().isEmpty();
        if (hasIgnoreList) {
            ignoreListBuilder.append(settingsConfig.getIgnoreListStored());
            ignoreListBuilder.append("\n");
            for (UUID ignoredPlayerUUID : user.getIgnoredPlayers()) {
                Player ignoredPlayer = Bukkit.getPlayer(ignoredPlayerUUID);
                if (ignoredPlayer != null) {
                    ignoreListBuilder.append("\n");
                    ignoreListBuilder.append(((TextComponent) ignoredPlayer.displayName()).content());
                }

            }
        } else {
            ignoreListBuilder.append(settingsConfig.getIgnoreListEmpty());
        }

        ignoreListBuilder.append("\n \n");
        ignoreListBuilder.append(
            hasIgnoreList
                ? user.isIgnoreAll()
                    ? "<#FF2200><click:run_command:'/ignore #clear #refresh'>[ Click To Clear ]</click></#FF2200> <#00FF29><click:run_command:'/ignore #all #refresh'>[ Toggle Ignore All ]</click></#00FF29>"
                    : "<#FF2200><click:run_command:'/ignore #clear #refresh'>[ Click To Clear ]</click> <click:run_command:'/ignore #all #refresh'>[ Toggle Ignore All ]</click></#FF2200>"
                : user.isIgnoreAll()
                    ? "<#00FF29><click:run_command:'/ignore #all #refresh'>[ Toggle Ignore All ]</click></#00FF29>"
                    : "<#FF2200><click:run_command:'/ignore #all #refresh'>[ Toggle Ignore All ]</click></#FF2200>"
        );

        ignoreListBuilder.append("\n \n");
        ignoreListBuilder.append(FOOTER);

        player.sendMessage(MiniMessage.miniMessage().deserialize(parseLegacyColorCodes(ignoreListBuilder.toString())));

    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {

        if (args.length == 2) {
            return null;
        }

        return List.of();
    }
}
