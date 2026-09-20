package com.johnymuffin.beta.discordauth.commands;

import com.johnymuffin.beta.discordauth.DiscordAuthentication;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DiscordAuthCommand implements CommandExecutor {
    private DiscordAuthentication plugin;

    public DiscordAuthCommand(DiscordAuthentication plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        String cmd = command.getName();
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Console can't run this command");
            return true;
        }

        Player player = (Player) commandSender;

        if (cmd.equalsIgnoreCase("discordauth")) {
            if (strings.length == 0) {
                player.sendMessage(formatchat("&4Incorrect Command: /discordauth [status]"));
                return true;
            }

            if (strings[0].equalsIgnoreCase("status")) {
                UUID uuid = player.getUniqueId();

                if (plugin.getData().isUUIDAlreadyLinked(uuid)) {
                    User discordUser = plugin.getDiscord().getDiscordBot().getJDA().getUserById(plugin.getData().getDiscordIDFromUUID(uuid));
                    if (discordUser == null) {
                        player.sendMessage(formatchat("&6Linked account found, but the Discord user is not currently visible to the bot."));
                        return true;
                    }
                    player.sendMessage(formatchat("&6Linked to: " + discordUser.getName()));
                } else {
                    player.sendMessage(formatchat("&4Sorry, we couldn't find a linked account to this UUID!"));
                }

            } else {
                player.sendMessage(formatchat("&4Incorrect Command: /discordauth [status]"));
            }

            return true;
        }


        return false;
    }

    public static String formatchat(String msg) {
        return msg.replaceAll("(&([a-f0-9]))", "\u00A7$2");
    }
}
