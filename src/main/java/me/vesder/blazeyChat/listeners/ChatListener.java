package me.vesder.blazeyChat.listeners;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.vesder.blazeyChat.commands.CommandManager;
import me.vesder.blazeyChat.commands.SubCommand;
import me.vesder.blazeyChat.configs.ConfigManager;
import me.vesder.blazeyChat.configs.customconfigs.FilterConfig;
import me.vesder.blazeyChat.configs.customconfigs.FormatConfig;
import me.vesder.blazeyChat.configs.customconfigs.SettingsConfig;
import me.vesder.blazeyChat.data.User;
import me.vesder.blazeyChat.data.UserManager;
import me.vesder.blazeyChat.hooks.VaultHook;
import me.vesder.blazeyChat.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import static me.vesder.blazeyChat.data.UserManager.getChatSpyPlayers;
import static me.vesder.blazeyChat.utils.Utils.buildFormattedComponent;

public class ChatListener implements Listener {

    FormatConfig formatConfig = (FormatConfig) ConfigManager.getConfigManager().getCustomConfig("format.yml");
    SettingsConfig settingsConfig = (SettingsConfig) ConfigManager.getConfigManager().getCustomConfig("settings.yml");
    FilterConfig filterConfig = (FilterConfig) ConfigManager.getConfigManager().getCustomConfig("filter.yml");

    @EventHandler
    private void onChat(AsyncChatEvent event) {

        Player player = event.getPlayer();

        if (!Utils.checkPermission(player, "blazeychat.chat")) {
            player.sendMessage(buildFormattedComponent(settingsConfig.getChatNoPermError(), player, null, null, null));
            event.setCancelled(true);
            return;
        }

        User user = UserManager.getUser(player.getUniqueId());
        String originalMessage = ((TextComponent) event.originalMessage()).content();
        SubCommand shoutCommand = CommandManager.getSubCommand("shout");

        // Check ( Block / Replace / Censor ) Words

        String originalMessageLowerCase = originalMessage.toLowerCase();

        for (String blockWords : filterConfig.getBlockWords()) {
            if (originalMessageLowerCase.contains(blockWords.toLowerCase())) {
                for (String action : filterConfig.getBlockActions()) {
                    Utils.runActionDispatcher(action, player, player, null, originalMessage, null);
                }
                event.setCancelled(true);
                return;
            }
        }

        for (String replaceWord : filterConfig.getReplaceWordsSection().getKeys(false)) {

            if (originalMessageLowerCase.contains(replaceWord.toLowerCase())) {
                for (String action : filterConfig.getReplaceActions()) {
                    Utils.runActionDispatcher(action, player, player, null, originalMessage, null);
                }
                originalMessage = originalMessage.replaceAll("(?i)" + Pattern.quote(replaceWord), Objects.requireNonNull(filterConfig.getReplaceWordsSection().getString(replaceWord)));
            }
        }

        for (String censorWord : filterConfig.getCensorWords()) {
            if (originalMessageLowerCase.contains(censorWord.toLowerCase())) {
                for (String action : filterConfig.getCensorActions()) {
                    Utils.runActionDispatcher(action, player, player, null, originalMessage, null);
                }
                originalMessage = originalMessage.replaceAll("(?i)" + Pattern.quote(censorWord), String.valueOf(filterConfig.getCensorChar()).repeat(censorWord.length()));
            }
        }

        boolean isMsgShout = user.isShout();

        if (originalMessage.startsWith(settingsConfig.getShoutFlag()) && Utils.checkPermission(player, shoutCommand.getPermission())) {
            originalMessage = originalMessage.substring(1).trim();
            isMsgShout = true;
        }

        setupViewers(event, isMsgShout, getChatSpyPlayers(),settingsConfig.isChatPerWorld() ? event.getPlayer().getWorld().getPlayers() : Collections.emptyList());

        List<String> playerGroups = List.of(VaultHook.getPerms().getPlayerGroups(player));
        List<String> formattedGroups = new ArrayList<>(formatConfig.getFormatSection().getKeys(false));
        Collections.reverse(formattedGroups);

        Component formatedMessage = null;
        for (String formattedGroup : formattedGroups) {

            if (!playerGroups.contains(formattedGroup)) {
                continue;
            }

            formatedMessage = buildFormattedComponent(formatConfig.getFormatSection().getString(formattedGroup), player, null, originalMessage, null);
            event.message(formatedMessage);
            break;
        }

        Component finalFormatedMessage = formatedMessage;
        String finalOriginalMessage = originalMessage;
        boolean finalIsMsgShout = isMsgShout;
        event.renderer((source, sourceDisplayName, message, viewer) -> {

            if (finalFormatedMessage == null) {

                Component defaultRender = ChatRenderer.defaultRenderer().render(source, sourceDisplayName, message, viewer);

                if (viewer instanceof ConsoleCommandSender) {
                    return Component.text("[" + source.getWorld().getName() + "] ").append(defaultRender);
                }

                if (finalIsMsgShout) {
                    return buildFormattedComponent(settingsConfig.getShoutFormat(), source, null, finalOriginalMessage, defaultRender);
                }

                if (getChatSpyPlayers().contains(viewer) && !viewer.equals(source)) {
                    return buildFormattedComponent(settingsConfig.getChatspyFormat(), source, null, finalOriginalMessage, defaultRender);
                }

                return defaultRender;
            }

            if (viewer instanceof ConsoleCommandSender) {
                return Component.text("[" + source.getWorld().getName() + "] ").append(finalFormatedMessage);
            }

            if (finalIsMsgShout) {
                return buildFormattedComponent(settingsConfig.getShoutFormat(), source, viewer instanceof Player ? (Player) viewer : null, finalOriginalMessage, finalFormatedMessage);
            }

            if (getChatSpyPlayers().contains(viewer) && !viewer.equals(source)) {
                return buildFormattedComponent(settingsConfig.getChatspyFormat(), source, viewer instanceof Player ? (Player) viewer : null, finalOriginalMessage, finalFormatedMessage);
            }

            return finalFormatedMessage;
        });

    }

    @SafeVarargs
    private void setupViewers(AsyncChatEvent event, boolean isShout, Collection<Player>... extraViewers) {

        if (settingsConfig.isChatPerWorld() && !isShout) {
            event.viewers().clear();
            event.viewers().add(Bukkit.getConsoleSender());
            for (Collection<Player> extraViewer : extraViewers) {
                event.viewers().addAll(extraViewer);
            }
        }

        UUID senderUUID = event.getPlayer().getUniqueId();

        event.viewers().removeIf(audience -> {

            if (audience instanceof Player viewerPlayer) {
                User viewerUser = UserManager.getUser(viewerPlayer.getUniqueId());
                Set<UUID> ignoredPlayers = viewerUser.getIgnoredPlayers();
                return viewerUser.isIgnoreAll() || (ignoredPlayers != null && ignoredPlayers.contains(senderUUID));
            }

            return false;
        });

    }
}
