package me.vesder.blazeyChat.utils;

import me.vesder.blazeyChat.configs.ConfigManager;
import me.vesder.blazeyChat.configs.customconfigs.SettingsConfig;
import me.vesder.blazeyChat.hooks.VaultHook;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Utils {

    private static final SettingsConfig settingsConfig = (SettingsConfig) ConfigManager.getConfigManager().getCustomConfig("settings.yml");

    private Utils() {}

    public static String parseLegacyColorCodes(String text) {

        text = text.replace("&0", "<black>");
        text = text.replace("&1", "<dark_blue>");
        text = text.replace("&2", "<dark_green>");
        text = text.replace("&3", "<dark_aqua>");
        text = text.replace("&4", "<dark_red>");
        text = text.replace("&5", "<dark_purple>");
        text = text.replace("&6", "<gold>");
        text = text.replace("&7", "<gray>");
        text = text.replace("&8", "<dark_gray>");
        text = text.replace("&9", "<blue>");
        text = text.replace("&a", "<green>");
        text = text.replace("&b", "<aqua>");
        text = text.replace("&c", "<red>");
        text = text.replace("&d", "<light_purple>");
        text = text.replace("&e", "<yellow>");
        text = text.replace("&f", "<white>");

        text = text.replace("&l", "<bold>");
        text = text.replace("&o", "<italic>");
        text = text.replace("&n", "<underlined>");
        text = text.replace("&m", "<strikethrough>");
        text = text.replace("&k", "<obfuscated>");
        text = text.replace("&r", "<reset>");

        return text;
    }

    public static Component buildFormattedComponent(String text, Player player, Player receiver, String originalMessage, Component formattedMessage) {

        text = parseLegacyColorCodes(text);

        boolean allowColor = checkPermission(player, "chatcorev.color");

        TagResolver resolver = TagResolver.resolver(
            Placeholder.component("message", originalMessage != null ? (allowColor ? MiniMessage.miniMessage().deserialize(parseLegacyColorCodes(originalMessage)) : Component.text(originalMessage)) : Component.empty()),
            Placeholder.component("formatted-message", formattedMessage != null ? formattedMessage : Component.empty()),
            Placeholder.component("prefix", MiniMessage.miniMessage().deserialize(parseLegacyColorCodes(settingsConfig.getDefaultPrefix()))),

            Placeholder.unparsed("username", player.getName()),
            Placeholder.component("displayname", player.displayName()),
            Placeholder.parsed("player-prefix", VaultHook.hasChat() ? VaultHook.getChat().getPlayerPrefix(player) : ""),
            Placeholder.parsed("player-suffix", VaultHook.hasChat() ? VaultHook.getChat().getPlayerSuffix(player) : ""),
            Placeholder.unparsed("group", VaultHook.hasPermissions() ? VaultHook.getPerms().getPrimaryGroup(player) : ""),
            Placeholder.unparsed("worldname", player.getWorld().getName()),

            Placeholder.unparsed("receiver-username", receiver != null ? receiver.getName() : ""),
            Placeholder.component("receiver-displayname", receiver != null ? receiver.displayName() : Component.empty()),
            Placeholder.parsed("receiver-prefix", receiver != null ? (VaultHook.hasChat() ? VaultHook.getChat().getPlayerPrefix(receiver) : "") : ""),
            Placeholder.parsed("receiver-suffix", receiver != null ? (VaultHook.hasChat() ? VaultHook.getChat().getPlayerSuffix(receiver) : "") : ""),
            Placeholder.unparsed("receiver-group", receiver != null ? (VaultHook.hasPermissions() ? VaultHook.getPerms().getPrimaryGroup(receiver) : "") : ""),
            Placeholder.unparsed("receiver-worldname", receiver != null ? receiver.getWorld().getName() : "")
        );

        return MiniMessage.miniMessage().deserialize(text, resolver);

    }

    public static void runActionDispatcher(String action, CommandSender target, Player player, Player receiver, String originalMessage, Component formattedMessage) {

        if (action.startsWith("CHAT:")) {
            action = action.substring(5).trim();
            target.sendMessage(buildFormattedComponent(action, player, receiver, originalMessage, formattedMessage));
            return;
        }

        // PLAYER ONLY
        if (!(target instanceof Player targetPlayer)) {
            return;
        }

        if (action.startsWith("ACTIONBAR:")) {
            action = action.substring(10).trim();
            targetPlayer.sendActionBar(buildFormattedComponent(action, player, receiver, originalMessage, formattedMessage));
            return;
        }

        if (action.startsWith("TITLE:")) { // sub title support later
            action = action.substring(6).trim();
            targetPlayer.showTitle(
                Title.title(
                    buildFormattedComponent(action, player, receiver, originalMessage, formattedMessage),
                    Component.empty()
                )
            );
            return;
        }

        if (action.startsWith("SOUND:")) {
            action = action.substring(6).trim();
            targetPlayer.playSound(targetPlayer.getLocation(), Sound.valueOf(action), 5.0F, 1.0F);
            return;
        }

        if (action.startsWith("COMMAND:")) {
            action = action.substring(8).trim();
            targetPlayer.performCommand(action);
            return;
        }
    }

    public static boolean checkPermission(Player player, String permission) {

        return player.hasPermission(permission) || VaultHook.hasPermissions() && VaultHook.getPerms().playerHas(player, permission);
    }
}
