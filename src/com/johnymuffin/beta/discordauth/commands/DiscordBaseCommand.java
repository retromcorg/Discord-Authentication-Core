package com.johnymuffin.beta.discordauth.commands;

import com.johnymuffin.beta.discordauth.DiscordAuthCache;
import com.johnymuffin.beta.discordauth.DiscordAuthConfig;
import com.johnymuffin.beta.discordauth.DiscordAuthDatafile;
import com.johnymuffin.beta.discordauth.DiscordAuthentication;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class DiscordBaseCommand implements CommandExecutor {
    protected DiscordAuthentication plugin;
    protected DiscordAuthCache cache;
    protected DiscordAuthConfig config;
    protected DiscordAuthDatafile data;

    public DiscordBaseCommand(DiscordAuthentication plugin) {
        this.plugin = plugin;
        this.cache = plugin.getCache();
        this.config = plugin.getConfig();
        this.data = plugin.getData();
    }


    protected boolean isAuthorized(CommandSender commandSender, String permission) {
        return (commandSender.isOp() || commandSender.hasPermission(permission));
    }

    // Remove first entry from string array
    protected String[] removeFirstEntry(String[] strings) {
        String[] newStrings = new String[strings.length - 1];
        System.arraycopy(strings, 1, newStrings, 0, strings.length - 1);
        return newStrings;
    }

    protected void sendWithNewline(CommandSender commandSender, String message) {
        String[] lines = message.split("\\n");
        for (String line : lines) {
            commandSender.sendMessage(line);
        }
    }

    protected String formatchat(String msg) {
        return msg.replaceAll("(&([a-f0-9]))", "\u00A7$2");
    }
}
