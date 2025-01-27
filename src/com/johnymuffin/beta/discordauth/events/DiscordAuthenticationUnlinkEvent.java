package com.johnymuffin.beta.discordauth.events;

import java.util.UUID;

public class DiscordAuthenticationUnlinkEvent extends DiscordAuthenticationUserEvent {
    public DiscordAuthenticationUnlinkEvent(UUID minecraftUUID, long discordID) {
        super("DiscordAuthenticationUnlinkEvent", minecraftUUID, discordID);
    }
}