package me.vesder.chatCoreV.listeners;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.vesder.chatCoreV.commands.CommandManager;
import me.vesder.chatCoreV.commands.SubCommand;
import me.vesder.chatCoreV.configs.ConfigManager;
import me.vesder.chatCoreV.configs.customconfigs.FilterConfig;
import me.vesder.chatCoreV.configs.customconfigs.FormatConfig;
import me.vesder.chatCoreV.configs.customconfigs.SettingsConfig;
import me.vesder.chatCoreV.data.User;
import me.vesder.chatCoreV.data.UserManager;
import me.vesder.chatCoreV.hooks.VaultHook;
import me.vesder.chatCoreV.utils.TextUtils;
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
import java.util.regex.Pattern;

import static me.vesder.chatCoreV.data.UserManager.getChatSpyPlayers;
import static me.vesder.chatCoreV.utils.TextUtils.buildFormattedComponent;

public class ChatListener implements Listener {

    FormatConfig formatConfig = (FormatConfig) ConfigManager.getConfigManager().getCustomConfig("format.yml");
    SettingsConfig settingsConfig = (SettingsConfig) ConfigManager.getConfigManager().getCustomConfig("settings.yml");
    FilterConfig filterConfig = (FilterConfig) ConfigManager.getConfigManager().getCustomConfig("filter.yml");

    @EventHandler
    private void onChat(AsyncChatEvent event) {

        Player player = event.getPlayer();

        if (!TextUtils.checkPermission(player, "chatcorev.chat")) {
            player.sendMessage(buildFormattedComponent("<prefix> <#FF2200>You are not allowed to chat.</#FF2200>", player, null, null, null));
            event.setCancelled(true);
            return;
        }

        SubCommand shoutCommand = CommandManager.getSubCommand("shout");
        String originalMessage = ((TextComponent) event.originalMessage()).content();
        User user = UserManager.getUser(player.getUniqueId());

        // Check ( Replace / Censor / Block ) Words

        String originalMessageLowerCase = originalMessage.toLowerCase();

        for (String replaceWord : filterConfig.getReplaceWordsSection().getKeys(false)) {

            if (originalMessageLowerCase.contains(replaceWord.toLowerCase())) {
                for (String action : filterConfig.getReplaceActions()) {
                    TextUtils.runActionDispatcher(action, player, player, null, originalMessage, null);
                }
                originalMessage = originalMessage.replaceAll("(?i)" + Pattern.quote(replaceWord), Objects.requireNonNull(filterConfig.getReplaceWordsSection().getString(replaceWord)));
            }
        }

        for (String censorWord : filterConfig.getCensorWords()) {
            if (originalMessageLowerCase.contains(censorWord.toLowerCase())) {
                for (String action : filterConfig.getCensorActions()) {
                    TextUtils.runActionDispatcher(action, player, player, null, originalMessage, null);
                }
                originalMessage = originalMessage.replaceAll("(?i)" + Pattern.quote(censorWord), String.valueOf(filterConfig.getCensorChar()).repeat(censorWord.length()));
            }
        }

        for (String blockWords : filterConfig.getBlockWords()) {
            if (originalMessageLowerCase.contains(blockWords.toLowerCase())) {
                for (String action : filterConfig.getBlockActions()) {
                    TextUtils.runActionDispatcher(action, player, player, null, originalMessage, null);
                }
                event.setCancelled(true);
                return;
            }
        }

        boolean isMsgShout;
        if (user.isShout()) {
            isMsgShout = true;
        } else if (originalMessage.startsWith(settingsConfig.getShoutFlag()) && TextUtils.checkPermission(player, shoutCommand.getPermission())) {
            originalMessage = originalMessage.substring(1).trim();
            isMsgShout = true;
        } else {
            isMsgShout = false;
            setupViewers(event, getChatSpyPlayers(), event.getPlayer().getWorld().getPlayers());
        }

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
        event.renderer((source, sourceDisplayName, message, viewer) -> {

            if (finalFormatedMessage == null) {

                Component defaultRender = ChatRenderer.defaultRenderer().render(source, sourceDisplayName, message, viewer);

                if (viewer instanceof ConsoleCommandSender) {
                    return Component.text("[" + source.getWorld().getName() + "] ").append(defaultRender);
                }

                if (isMsgShout) {
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

            if (isMsgShout) {
                return buildFormattedComponent(settingsConfig.getShoutFormat(), source, viewer instanceof Player ? (Player) viewer : null, finalOriginalMessage, finalFormatedMessage);
            }

            if (getChatSpyPlayers().contains(viewer) && !viewer.equals(source)) {
                return buildFormattedComponent(settingsConfig.getChatspyFormat(), source, viewer instanceof Player ? (Player) viewer : null, finalOriginalMessage, finalFormatedMessage);
            }

            return finalFormatedMessage;
        });

    }

    // make it more dynamic later
    @SafeVarargs
    private void setupViewers(AsyncChatEvent event, Collection<Player>... extraViewers) {

        event.viewers().clear();
        event.viewers().add(Bukkit.getConsoleSender());
        for (Collection<Player> extraViewer : extraViewers) {
            event.viewers().addAll(extraViewer);
        }

    }
}
