package com.johnymuffin.beta.discordauth;

import com.johnymuffin.beta.discordauth.events.DiscordAuthenticationLinkEvent;
import com.johnymuffin.beta.discordauth.events.DiscordAuthenticationUnlinkEvent;
import org.bukkit.Bukkit;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class DiscordAuthDatafile {
    private DiscordAuthentication plugin;
    private int taskID;
    private Configuration config;
    private HashMap<String, HashMap<String, String>> discordAuthDatabase = new HashMap<String, HashMap<String, String>>();


    public DiscordAuthDatafile(DiscordAuthentication plugin) {
        this.plugin = plugin;
        taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                saveConfig();
            }
        }, 0L, 20 * 60 * 10);
        config = new Configuration(new File(plugin.getDataFolder(), "data.yml"));
        config.load();
        if (config.getProperty("authentication") == null) {
            config.setProperty("authentication", (Object) new HashMap());
        }
        discordAuthDatabase = (HashMap<String, HashMap<String, String>>) config.getProperty("authentication");

    }

    @Deprecated
    public boolean isUUIDAlreadyLinked(String uuid) {
        if (discordAuthDatabase.containsKey(uuid)) {
            return true;
        }
        return false;
    }

    public boolean isUUIDAlreadyLinked(UUID uuid) {
        return isUUIDAlreadyLinked(uuid.toString());
    }

    @Deprecated
    public boolean isDiscordIDAlreadyLinked(String discordID) {

        //Iterate through all keys
        for (String key1 : discordAuthDatabase.keySet()) {
            if (discordAuthDatabase.get(key1).get("discordID").equals(discordID)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDiscordIDAlreadyLinked(long discordID) {
        return isDiscordIDAlreadyLinked(String.valueOf(discordID));
    }

    @Deprecated
    public boolean removeLinkFromDiscordID(String discordID) {
        //Iterate through all keys
        for (String key1 : discordAuthDatabase.keySet()) {
            if (discordAuthDatabase.get(key1).get("discordID").equals(discordID)) {
                long DiscordID = Long.parseLong(discordID);
                UUID minecraftUUID = UUID.fromString(key1);

                // Call DiscordAuthenticationUnlinkEvent event in the next tick
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    // Call DiscordAuthenticationUnlinkEvent event
                    DiscordAuthenticationUnlinkEvent event = new DiscordAuthenticationUnlinkEvent(minecraftUUID, DiscordID);
                    Bukkit.getServer().getPluginManager().callEvent(event);
                });

                discordAuthDatabase.remove(key1);
                return true;
            }
        }
        return false;
    }

    public boolean removeLinkFromDiscordID(long discordID) {
        return removeLinkFromDiscordID(String.valueOf(discordID));
    }

    @Deprecated
    public boolean removeLinkByUUID(String uuid) {
        if (discordAuthDatabase.containsKey(uuid)) {
            long discordID = Long.parseLong(discordAuthDatabase.get(uuid).get("discordID"));
            discordAuthDatabase.remove(uuid);

            // Call DiscordAuthenticationUnlinkEvent event in the next tick
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                // Call DiscordAuthenticationUnlinkEvent event
                DiscordAuthenticationUnlinkEvent event = new DiscordAuthenticationUnlinkEvent(UUID.fromString(uuid), discordID);
                Bukkit.getServer().getPluginManager().callEvent(event);
            });

            return true;
        }
        return false;
    }

    public boolean removeLinkByUUID(UUID uuid) {
        return removeLinkByUUID(uuid.toString());
    }


    @Deprecated
    public boolean addLinkedUser(String username, String uuid, String discordID) {
        if (isUUIDAlreadyLinked(uuid) || isDiscordIDAlreadyLinked(discordID)) {
            //If user is already linked return false
            return false;
        }

        final HashMap<String, String> tmp = new HashMap<String, String>();
        tmp.put("username", username);
        tmp.put("discordID", discordID);

        discordAuthDatabase.put(uuid, tmp);

        // Call DiscordAuthenticationLinkEvent event
        DiscordAuthenticationLinkEvent event = new DiscordAuthenticationLinkEvent(UUID.fromString(uuid), Long.parseLong(discordID));
        Bukkit.getServer().getPluginManager().callEvent(event);

        return true;
    }

    public boolean addLinkedUser(String username, UUID uuid, long discordID) {
        return addLinkedUser(username, uuid.toString(), String.valueOf(discordID));
    }

    @Deprecated
    public String getDiscordIDFromUUID(String uuid) {
        if (!discordAuthDatabase.containsKey(uuid)) {
            return null;
        } else {
            return discordAuthDatabase.get(uuid).get("discordID");
        }
    }

    public long getDiscordIDFromUUID(UUID uuid) {
        return Long.parseLong(getDiscordIDFromUUID(uuid.toString()));
    }

    @Deprecated
    public String getInitialUsernameFromDiscordID(String discordID) {
        return getLastUsernameFromDiscordID(discordID);
    }

    @Deprecated
    public String getLastUsernameFromDiscordID(String discordID) {
        if (!isDiscordIDAlreadyLinked(discordID)) {
            return null;
        }
        String uuid = getUUIDFromDiscordID(discordID);
        if (!discordAuthDatabase.containsKey(uuid)) {
            return null;
        }
        return discordAuthDatabase.get(uuid).get("username");
    }

    public String getLastUsernameFromDiscordID(long discordID) {
        return getLastUsernameFromDiscordID(String.valueOf(discordID));
    }

    @Deprecated
    public String getUUIDFromDiscordID(String discordID) {
        for (String key1 : discordAuthDatabase.keySet()) {
            if (discordAuthDatabase.get(key1).get("discordID").equals(discordID)) {
                return key1;
            }
        }
        return null;
    }

    public UUID getUUIDFromDiscordID(long discordID) {
        return UUID.fromString(getUUIDFromDiscordID(String.valueOf(discordID)));
    }

    public void updateLastKnownUsername(UUID uuid, String username) {
        String uuid2 = uuid.toString();
        if (!discordAuthDatabase.containsKey(uuid2)) {
            return;
        }
        if (!discordAuthDatabase.get(uuid2).get("username").equals(username)) {
            discordAuthDatabase.get(uuid2).replace("username", username);
        }
    }

    public void saveConfig() {
        config.setProperty("authentication", discordAuthDatabase);
        config.save();
    }
}
