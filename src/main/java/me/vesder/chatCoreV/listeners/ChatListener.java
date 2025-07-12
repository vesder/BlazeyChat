package me.vesder.chatCoreV.listeners;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.vesder.chatCoreV.configs.ConfigManager;
import me.vesder.chatCoreV.configs.customconfigs.FormatConfig;
import me.vesder.chatCoreV.data.User;
import me.vesder.chatCoreV.data.UserManager;
import me.vesder.chatCoreV.hooks.VaultHook;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static me.vesder.chatCoreV.utils.TextUtils.parseLegacyColorCodes;

public class ChatListener implements Listener {

    FormatConfig formatConfig = (FormatConfig) ConfigManager.getConfigManager().getCustomConfig("format.yml");

    @EventHandler
    private void onChat(AsyncChatEvent event) {

//        System.out.println("CHAT " + VaultHook.hasChat());
//        System.out.println("ECO " + VaultHook.hasEconomy());

        Player player = event.getPlayer();

        setupViewers(event, player, getChatSpyPlayers(), event.getPlayer().getWorld().getPlayers());

//        if (VaultHook.hasPermissions()) {
//            Later Fix Soft Depend Vault + @Getter For Vault Hook
//        }

        List<String> playerGroups = List.of(VaultHook.getPerms().getPlayerGroups(player));
        List<String> formattedGroups = new ArrayList<>(formatConfig.getFormatSection().getKeys(false));
        Collections.reverse(formattedGroups);

        Component formatedMessage = null;
        for (String formattedGroup : formattedGroups) {

            if (!playerGroups.contains(formattedGroup)) {
                continue;
            }

            formatedMessage = buildFormattedComponent(formatConfig.getFormatSection().getString(formattedGroup), player, event);
            event.message(formatedMessage);
            break;
        }

        Component finalFormatedMessage = formatedMessage;
        event.renderer((source, sourceDisplayName, message, viewer) -> {

            if (finalFormatedMessage == null) {

                Component defaultRender = ChatRenderer.defaultRenderer().render(source, sourceDisplayName, message, viewer);

                if (viewer instanceof ConsoleCommandSender) {
                    return Component.text("[" + source.getWorld().getName() + "] ").append(defaultRender);
                }

                if (getChatSpyPlayers().contains(viewer) && !viewer.equals(source)) {
                    return Component.text("[SPY]" + " [" + source.getWorld().getName() + "] ").append(defaultRender);
                }

                return defaultRender;
            }

            if (viewer instanceof ConsoleCommandSender) {
                return Component.text("[" + source.getWorld().getName() + "] ").append(finalFormatedMessage);
            }

            if (getChatSpyPlayers().contains(viewer) && !viewer.equals(source)) {
                return Component.text("[SPY]" + " [" + source.getWorld().getName() + "] ").append(finalFormatedMessage);
            }

            return finalFormatedMessage;
        });

    }

    // make it more dynamic later
    @SafeVarargs
    private void setupViewers(AsyncChatEvent event, Player player, Collection<Player>... extraViewers) {

        if (UserManager.getUser(player.getUniqueId()).isShout()) {
            return;
        }

        event.viewers().clear();
        event.viewers().add(Bukkit.getConsoleSender());
        for (Collection<Player> extraViewer : extraViewers) {
            event.viewers().addAll(extraViewer);
        }

    }

    private Component buildFormattedComponent(String text, Player player, AsyncChatEvent event) {

        text = parseLegacyColorCodes(text);

        boolean allowColor = player.hasPermission("chatcorev.color");

        if (!allowColor && VaultHook.hasPermissions()) {
            allowColor = VaultHook.getPerms().playerHas(player, "chatcorev.color");
        }

        if (allowColor) {

            return MiniMessage.miniMessage().deserialize(text,
                Placeholder.component("message", MiniMessage.miniMessage().deserialize(parseLegacyColorCodes(((TextComponent) event.originalMessage()).content()))), //bug
                Placeholder.unparsed("username", player.getName()),
                Placeholder.component("displayname", player.displayName()),
                Placeholder.parsed("prefix", VaultHook.getChat().getPlayerPrefix(player)),
                Placeholder.parsed("suffix", VaultHook.getChat().getPlayerSuffix(player)),
                Placeholder.unparsed("group", VaultHook.getPerms().getPrimaryGroup(player)),
                Placeholder.unparsed("worldname", player.getWorld().getName())
            );

        }

        return MiniMessage.miniMessage().deserialize(text,
            Placeholder.component("message", event.originalMessage()), // without color
            Placeholder.unparsed("username", player.getName()),
            Placeholder.component("displayname", player.displayName()),
            Placeholder.parsed("prefix", VaultHook.getChat().getPlayerPrefix(player)),
            Placeholder.parsed("suffix", VaultHook.getChat().getPlayerSuffix(player)),
            Placeholder.unparsed("group", VaultHook.getPerms().getPrimaryGroup(player)),
            Placeholder.unparsed("worldname", player.getWorld().getName())
        );

    }

    private Set<Player> getChatSpyPlayers() {

        Set<Player> set = new HashSet<>();

        for (Map.Entry<UUID, User> user : UserManager.userMap.entrySet()) {

            if (user.getValue().isChatSpy()) {
                set.add(Bukkit.getPlayer(user.getKey()));
            }
        }

        return set;
    }
}
