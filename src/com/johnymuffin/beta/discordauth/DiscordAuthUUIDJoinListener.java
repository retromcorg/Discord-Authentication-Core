package com.johnymuffin.beta.discordauth;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DiscordAuthUUIDJoinListener extends CustomEventListener implements Listener {
    private DiscordAuthentication plugin;

    public DiscordAuthUUIDJoinListener(DiscordAuthentication plugin) {
        this.plugin = plugin;

    }

    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        plugin.getData().updateLastKnownUsername(event.getPlayer().getUniqueId(), event.getPlayer().getName());
    }

}
