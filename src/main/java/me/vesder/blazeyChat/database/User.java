package me.vesder.blazeyChat.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    // Player info
    private String username;

    // Boolean flags
    private boolean shout;
    private boolean chatSpy;
    private boolean ignoreAll;

    // Collections
    private Set<UUID> ignoredPlayers;
    private Set<UUID> blockedPlayers;

    // References
    private UUID lastMsgSender;
    private UUID replyTarget;

}
