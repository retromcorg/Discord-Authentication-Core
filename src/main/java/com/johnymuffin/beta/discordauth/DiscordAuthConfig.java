package com.johnymuffin.beta.discordauth;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DiscordAuthConfig extends Configuration {

    private JavaPlugin plugin;


    public DiscordAuthConfig(JavaPlugin plugin) {
        super(new File(plugin.getDataFolder(), "config.yml"));
        this.plugin = plugin;
        this.reload();
    }

    private void write() {
        generateConfigOption("config-version", 1);


        generateConfigOption("settings.discord.automatic-nickname.enabled", false);
        generateConfigOption("settings.discord.automatic-nickname.info", "This will automatically set the nickname of the user to their minecraft username.");

        // Setting that sends linking message via DM as opposed to linking channel
        generateConfigOption("settings.discord.send-linking-code-via-dm.info", "This will send the linking code via DM instead of in the channel the command was run in. Sending via DMs can be problematic if users have DMs disabled.");
        generateConfigOption("settings.discord.send-linking-code-via-dm.enabled", false);

        getAutomaticNicknameGuilds();

    }

    public List<Object> getAutomaticNicknameGuilds() {
        String key = "settings.discord.automatic-nickname.guilds";
        if (this.getList(key) == null) {
            List<String> defaultItems = new ArrayList<String>();
            defaultItems.add("0");

            this.setProperty(key, defaultItems);
        }
        return this.getList(key);
    }

    public void reload() {
        this.load();
        this.write();
        this.save();
    }

    //Getters Start
    public Object getConfigOption(String key) {
        return this.getProperty(key);
    }

    public String getConfigString(String key) {
        return String.valueOf(getConfigOption(key));
    }

    public Integer getConfigInteger(String key) {
        return Integer.valueOf(getConfigString(key));
    }

    public Long getConfigLong(String key) {
        return Long.valueOf(getConfigString(key));
    }

    public Double getConfigDouble(String key) {
        return Double.valueOf(getConfigString(key));
    }

    public Boolean getConfigBoolean(String key) {
        return Boolean.valueOf(getConfigString(key));
    }


    //Getters End

    public void generateConfigOption(String key, Object defaultValue) {
        if (this.getProperty(key) == null) {
            this.setProperty(key, defaultValue);
        }
        final Object value = this.getProperty(key);
        this.removeProperty(key);
        this.setProperty(key, value);
    }


    public Long getConfigLongOption(String key) {
        if (this.getConfigOption(key) == null) {
            return null;
        }
        return Long.valueOf(String.valueOf(this.getProperty(key)));
    }


    private boolean convertToNewAddress(String newKey, String oldKey) {
        if (this.getString(newKey) != null) {
            return false;
        }
        if (this.getString(oldKey) == null) {
            return false;
        }
        System.out.println("Converting Config: " + oldKey + " to " + newKey);
        Object value = this.getProperty(oldKey);
        this.setProperty(newKey, value);
        this.removeProperty(oldKey);
        return true;

    }


}