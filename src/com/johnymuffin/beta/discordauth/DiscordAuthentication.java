package com.johnymuffin.beta.discordauth;

import com.johnymuffin.beta.discordauth.commands.DiscordAuthCommand;
import com.johnymuffin.discordcore.DiscordCore;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class DiscordAuthentication extends JavaPlugin {
    private Logger log;
    private PluginDescriptionFile pdf;
    private DiscordAuthentication plugin;
    public static DiscordCore discord;
    private DiscordAuthCache cache;
    public DiscordAuthDatafile data;

    @Override
    public void onEnable() {
        log = this.getServer().getLogger();
        pdf = this.getDescription();
        plugin = this;
        log.info("[" + pdf.getName() + "] Is loading, Version: " + pdf.getVersion() + " | Bukkit: " + Bukkit.getServer().getVersion());
        //Enabling
        PluginManager pm = Bukkit.getServer().getPluginManager();
        if (pm.getPlugin("DiscordCore") == null) {
            log.info("}---------------ERROR---------------{");
            log.info("World Edit AuditLog Requires Discord Core");
            log.info("Download it at: https://github.com/RhysB/Discord-Bot-Core");
            log.info("}---------------ERROR---------------{");
            log.info("World Edit AuditLog Is Shutting Down Forcefully");
            pm.disablePlugin(this);
            return;
        }


        discord = (DiscordCore) getServer().getPluginManager().getPlugin("DiscordCore");
        data = new DiscordAuthDatafile(plugin);
        cache = new DiscordAuthCache(plugin);


        final DiscordAuthListener DAL = new DiscordAuthListener(plugin);
        discord.Discord().jda.addEventListener(DAL);

        final DiscordAuthenticationCommands DAC = new DiscordAuthenticationCommands(plugin);


    }

    @Override
    public void onDisable() {
        data.saveConfig();
        log.info("[" + pdf.getName() + "] Has Been Disabled");
    }

    public DiscordCore getDiscord() {
        return discord;
    }

    public DiscordAuthCache getCache() {
        return cache;
    }

    public DiscordAuthDatafile getData() {
        return data;
    }
}

class DiscordAuthenticationCommands{
    DiscordAuthentication plugin;
    public DiscordAuthenticationCommands(DiscordAuthentication plugin) {
        this.plugin = plugin;
        plugin.getCommand("discordauth").setExecutor(new DiscordAuthCommand(plugin));
    }

}
