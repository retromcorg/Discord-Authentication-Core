package com.johnymuffin.beta.discordauth.events;

import org.bukkit.event.HandlerList;

import java.util.UUID;

public class DiscordAuthenticationUnlinkEvent extends DiscordAuthenticationUserEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public DiscordAuthenticationUnlinkEvent(UUID minecraftUUID, long discordID) {
        super(minecraftUUID, discordID);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
