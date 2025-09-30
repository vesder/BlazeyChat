package me.vesder.blazeyChat.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class UserManager {

    public static final Map<UUID, User> userMap = new HashMap<>();

    public static User getUser(UUID uuid) {

        if (userMap.containsKey(uuid)) {
            return userMap.get(uuid);
        }

        userMap.put(uuid, new User());
        return userMap.get(uuid);

    }

    public static Set<Player> getChatSpyPlayers() {

        Set<Player> set = new HashSet<>();

        for (Map.Entry<UUID, User> user : UserManager.userMap.entrySet()) {

            if (user.getValue().isChatSpy() && Bukkit.getPlayer(user.getKey()) != null) {
                set.add(Bukkit.getPlayer(user.getKey()));
            }
        }

        return set;
    }

}
