package me.vesder.blazeyChat.listeners;

import me.vesder.blazeyChat.configs.customconfigs.SettingsConfig;
import me.vesder.blazeyChat.database.User;
import me.vesder.blazeyChat.database.UserManager;
import me.vesder.blazeyChat.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final SettingsConfig settingsConfig;

    public JoinListener(SettingsConfig settingsConfig) {
        this.settingsConfig = settingsConfig;
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (settingsConfig.isChatspyOnJoin()) {
            if (Utils.checkPermission(player, "blazeychat.command.chatspy")) {
                User user = UserManager.getUser(player.getUniqueId());
                user.setChatSpy(true);
            }
        }

    }

}
