package me.vesder.chatCoreV.data;

import java.util.HashMap;
import java.util.Map;
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

}
