package me.vesder.blazeyChat.listeners;

import me.vesder.blazeyChat.database.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UserManager.saveAndUnloadUser(player.getUniqueId());
    }

}
