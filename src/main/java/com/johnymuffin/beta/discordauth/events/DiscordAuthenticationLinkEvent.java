package com.johnymuffin.beta.discordauth.events;

import java.util.UUID;

public class DiscordAuthenticationLinkEvent extends DiscordAuthenticationUserEvent {
    public DiscordAuthenticationLinkEvent(UUID minecraftUUID, long discordID) {
        super("DiscordAuthenticationLinkEvent", minecraftUUID, discordID);
    }
}