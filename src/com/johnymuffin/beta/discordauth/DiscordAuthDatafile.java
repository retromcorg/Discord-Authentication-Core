package com.johnymuffin.beta.discordauth;

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

    public boolean isUUIDAlreadyLinked(String uuid) {
        if (discordAuthDatabase.containsKey(uuid)) {
            return true;
        }
        return false;
    }

    public boolean isDiscordIDAlreadyLinked(String discordID) {

        //Iterate through all keys
        for (String key1 : discordAuthDatabase.keySet()) {
            if (discordAuthDatabase.get(key1).get("discordID").equals(discordID)) {
                return true;
            }
        }
        return false;
    }

    public boolean removeLinkFromDiscordID(String discordID) {
        //Iterate through all keys
        for (String key1 : discordAuthDatabase.keySet()) {
            if (discordAuthDatabase.get(key1).get("discordID").equals(discordID)) {
                discordAuthDatabase.remove(key1);
                return true;
            }
        }
        return false;
    }

    public boolean removeLinkByUUID(String uuid) {
        if (discordAuthDatabase.containsKey(uuid)) {
            discordAuthDatabase.remove(uuid);
            return true;
        }
        return false;
    }


    public boolean addLinkedUser(String username, String uuid, String discordID) {
        if (isUUIDAlreadyLinked(uuid) || isDiscordIDAlreadyLinked(discordID)) {
            //If user is already linked return false
            return false;
        }

        final HashMap<String, String> tmp = new HashMap<String, String>();
        tmp.put("username", username);
        tmp.put("discordID", discordID);

        discordAuthDatabase.put(uuid, tmp);
        return true;
    }

    public String getDiscordIDFromUUID(String uuid) {
        if (!discordAuthDatabase.containsKey(uuid)) {
            return null;
        } else {
            return discordAuthDatabase.get(uuid).get("discordID");
        }


    }

    @Deprecated
    public String getInitialUsernameFromDiscordID(String discordID) {
        return getLastUsernameFromDiscordID(discordID);
    }

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


    public String getUUIDFromDiscordID(String discordID) {
        for (String key1 : discordAuthDatabase.keySet()) {
            if (discordAuthDatabase.get(key1).get("discordID").equals(discordID)) {
                return key1;
            }
        }
        return null;
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
