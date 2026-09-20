package com.johnymuffin.beta.discordauth;

import com.johnymuffin.beta.discordauth.commands.DiscordAuthCommand;
import com.johnymuffin.beta.discordauth.commands.DiscordLinkCommand;
import com.johnymuffin.beta.discordauth.commands.DiscordUnlinkCommand;
import org.retromc.discordcore.v6.DiscordCorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiscordAuthentication extends JavaPlugin {
    private Logger log;
    private PluginDescriptionFile pdf;
    private DiscordAuthentication plugin;
    public static DiscordCorePlugin discord;
    private DiscordAuthCache cache;
    private String pluginName;
    public DiscordAuthDatafile data;

    private DiscordAuthConfig config;
    private com.johnymuffin.beta.discordauth.discordcommands.DiscordAuthListener discordCommandListener;

    @Override
    public void onEnable() {
        log = this.getServer().getLogger();
        pdf = this.getDescription();
        plugin = this;
        pluginName = pdf.getName();
        log.info("[" + pdf.getName() + "] Is loading, Version: " + pdf.getVersion() + " | Bukkit: " + Bukkit.getServer().getVersion());
        //Enabling
        PluginManager pm = Bukkit.getServer().getPluginManager();
        Plugin discordPlugin = pm.getPlugin("DiscordCore-6");
        if (!(discordPlugin instanceof DiscordCorePlugin)) {
            log.info("}---------------ERROR---------------{");
            log.info("Discord Authentication requires DiscordCore-6.");
            log.info("}---------------ERROR---------------{");
            log.info("Discord Authentication Is Shutting Down Forcefully");
            pm.disablePlugin(this);
            return;
        }

        discord = (DiscordCorePlugin) discordPlugin;
        if (discord.getDiscordBot() == null || discord.getDiscordBot().getJDA() == null) {
            log.info("DiscordCore-6 is enabled, but its JDA instance is unavailable.");
            pm.disablePlugin(this);
            return;
        }

        data = new DiscordAuthDatafile(plugin);
        cache = new DiscordAuthCache(plugin);
        config = new DiscordAuthConfig(plugin);

        discordCommandListener = new com.johnymuffin.beta.discordauth.discordcommands.DiscordAuthListener(plugin);
        discord.getDiscordBot().getJDA().addEventListener(discordCommandListener);

        // Register Commands
        plugin.getCommand("discordauth").setExecutor(new DiscordAuthCommand(plugin)); // Deprecated Command
        plugin.getCommand("link").setExecutor(new DiscordLinkCommand(plugin));
        plugin.getCommand("unlink").setExecutor(new DiscordUnlinkCommand(plugin));


        //Update Last Known Username Logic
        getServer().getPluginManager().registerEvents(new DiscordAuthListener(plugin), plugin);


    }

    @Override
    public void onDisable() {
        if (discord != null && discordCommandListener != null && discord.getDiscordBot() != null && discord.getDiscordBot().getJDA() != null) {
            discord.getDiscordBot().getJDA().removeEventListener(discordCommandListener);
        }
        if (data != null) {
            data.saveConfig();
        }
        log.info("[" + pdf.getName() + "] Has Been Disabled");
    }

    public void logger(Level level, String message) {
        log.log(level, "[" + pdf.getName() + "] " + message);
    }

    public DiscordCorePlugin getDiscord() {
        return discord;
    }

    public DiscordAuthCache getCache() {
        return cache;
    }

    public DiscordAuthDatafile getData() {
        return data;
    }

    public void logInfo(String s) {
        log.info("[" + pluginName + "] " + s);
    }

    public UUID getPlayerUUID(String playerName) {
        OfflinePlayer cachedPlayer = Bukkit.getOfflinePlayerIfCached(playerName);
        if (cachedPlayer != null) {
            return cachedPlayer.getUniqueId();
        }
        return Bukkit.getServer().createOfflineProfile(playerName).getUniqueId();
    }

    public DiscordAuthConfig getConfig() {
        return config;
    }
}
