package com.johnymuffin.beta.discordauth.commands;

import com.johnymuffin.beta.discordauth.DiscordAuthentication;
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
                UUID uuid = plugin.getPlayerUUID(player.getName());
                if (uuid == null) {
                    //Players UUID can't be found
                    player.sendMessage(formatchat("&4Sorry, we can't find a UUID corresponding to your account\nPlease make sure you are using a premium account\nIf the issue persists please contact staff!"));
                    return true;
                }

                if (plugin.getData().isUUIDAlreadyLinked(uuid.toString())) {
                    String discordDisplayName = plugin.getDiscord().getDiscordBot().jda.getUserById(plugin.getData().getDiscordIDFromUUID(uuid.toString())).getName() + "#" + plugin.getDiscord().getDiscordBot().jda.getUserById(plugin.getData().getDiscordIDFromUUID(uuid.toString())).getDiscriminator();
                    player.sendMessage(formatchat("&6Linked to: " + discordDisplayName));
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
