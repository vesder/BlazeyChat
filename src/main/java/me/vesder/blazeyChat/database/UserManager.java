package me.vesder.blazeyChat.database;

import me.vesder.blazeyChat.BlazeyChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class UserManager {

    public static final Map<UUID, User> userMap = new HashMap<>();

    public static User getUser(UUID uuid) {

        if (userMap.containsKey(uuid)) {
            return userMap.get(uuid);
        }

        // check if player is already have data in database
        try {
            UserDatabase userDatabase = UserDatabase.getInstance();
            if (userDatabase.userExists(uuid)) {
                userMap.put(uuid, userDatabase.getUserData(uuid));
                userMap.get(uuid).setUsername(Objects.requireNonNull(Bukkit.getPlayer(uuid)).getName());
                return userMap.get(uuid);
            }
        } catch (SQLException ex) {
            BlazeyChat.getPlugin().getLogger().log(Level.WARNING, "Failed to connect to database!", ex);
        }

        userMap.put(uuid, new User());
        userMap.get(uuid).setUsername(Objects.requireNonNull(Bukkit.getPlayer(uuid)).getName());
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
