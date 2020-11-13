package com.johnymuffin.beta.discordauth;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class DiscordAuthUUIDJoinListener extends PlayerListener {
    private DiscordAuthentication plugin;

    public DiscordAuthUUIDJoinListener(DiscordAuthentication plugin) {
        this.plugin = plugin;

    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getData().updateLastKnownUsername(event.getPlayer().getUniqueId(), event.getPlayer().getName());
    }

}
