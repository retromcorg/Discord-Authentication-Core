package com.johnymuffin.beta.discordauth;

import com.johnymuffin.uuidcore.event.PlayerUUIDEvent;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public class DiscordAuthUUIDJoinListener extends CustomEventListener implements Listener {
    private DiscordAuthentication plugin;

    public DiscordAuthUUIDJoinListener(DiscordAuthentication plugin) {
        this.plugin = plugin;

    }

    @Override
    public void onCustomEvent(Event event) {
        if (event instanceof PlayerUUIDEvent) {
            if (!((PlayerUUIDEvent) event).getUUIDStatus()) {
                //Check if UUID get was successful
                return;
            }
            if(((PlayerUUIDEvent) event).getPlayerUUID() == null || ((PlayerUUIDEvent) event).getPlayer() == null) {
                return;
            }
            plugin.getData().updateLastKnownUsername(((PlayerUUIDEvent) event).getPlayerUUID().toString(), ((PlayerUUIDEvent) event).getPlayer().getName());

        }
    }
}
