package me.vesder.chatCoreV.data;

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

    private boolean shout;
    private boolean chatSpy;
    private String adsMusic;
    private Set<UUID> ignoredPlayers;
    private Set<UUID> blockedPlayers;
    private UUID lastMsgSender;
    private UUID replyTarget;

}
