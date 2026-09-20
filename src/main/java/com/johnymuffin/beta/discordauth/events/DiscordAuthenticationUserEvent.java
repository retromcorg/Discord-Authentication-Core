package com.johnymuffin.beta.discordauth.events;

import org.bukkit.event.Event;

import java.util.UUID;

public abstract class DiscordAuthenticationUserEvent extends Event {
    private final UUID minecraftUUID;
    private final long discordID;

    protected DiscordAuthenticationUserEvent(UUID minecraftUUID, long discordID) {
        super();
        this.minecraftUUID = minecraftUUID;
        this.discordID = discordID;
    }

    public UUID getMinecraftUUID() {
        return minecraftUUID;
    }

    public long getDiscordID() {
        return discordID;
    }
}
