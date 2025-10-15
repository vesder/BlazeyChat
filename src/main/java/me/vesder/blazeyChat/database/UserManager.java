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

    public static final Map<UUID, User> userCache = new HashMap<>();
    private static final UserDatabase userDatabase = UserDatabase.getInstance();

    public static User loadUser(UUID uuid) {

        if (userCache.containsKey(uuid)) {
            return userCache.get(uuid);
        }

        // check if player is already have data in database
        try {
            if (userDatabase.userExists(uuid)) {
                userCache.put(uuid, userDatabase.getUserData(uuid));
                userCache.get(uuid).setUsername(Objects.requireNonNull(Bukkit.getPlayer(uuid)).getName());
                return userCache.get(uuid);
            }
        } catch (SQLException ex) {
            BlazeyChat.getPlugin().getLogger().log(Level.WARNING, "Failed to connect to database!", ex);
        }

        userCache.put(uuid, new User());
        userCache.get(uuid).setUsername(Objects.requireNonNull(Bukkit.getPlayer(uuid)).getName());
        return userCache.get(uuid);
    }

    public static void saveAndUnloadUser(UUID uuid) {
        try {
            userDatabase.saveUserData(uuid, loadUser(uuid));
        } catch (SQLException ex) {
            BlazeyChat.getPlugin().getLogger().log(Level.WARNING, "Failed to save user data to database!", ex);
        }
        userCache.remove(uuid);
    }

    public static Set<Player> getChatSpyPlayers() {

        Set<Player> set = new HashSet<>();

        for (Map.Entry<UUID, User> user : UserManager.userCache.entrySet()) {

            if (user.getValue().isChatSpy() && Bukkit.getPlayer(user.getKey()) != null) {
                set.add(Bukkit.getPlayer(user.getKey()));
            }
        }

        return set;
    }

}
