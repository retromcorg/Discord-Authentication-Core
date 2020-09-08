package com.johnymuffin.beta.discordauth.commands;

import com.johnymuffin.beta.discordauth.DiscordAuthentication;
import com.projectposeidon.api.PoseidonUUID;
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
                player.sendMessage(formatchat("&4Incorrect Command: /discordauth [link,unlink,status] [code]"));
                return true;
            }

            if (strings[0].equalsIgnoreCase("link")) {
                if (!(strings.length >= 2)) {
                    player.sendMessage(formatchat("&4Please specify a code /discordauth link (code)"));
                    return true;
                }
                //Get UUID
                UUID uuid = PoseidonUUID.getPlayerMojangUUID(player.getName());
                if (uuid == null) {
                    //Players UUID can't be found
                    player.sendMessage(formatchat("&4Sorry, we can't find a UUID corresponding to your account\nPlease make sure you are using a premium account\nIf the issue persists please contact staff!"));
                    return true;
                }

                if (!plugin.getCache().isUserPending(uuid.toString())) {
                    //Player does not have a pending code
                    player.sendMessage(formatchat("&4Please start the linking process on Discord\nPlease run \"!link (username)\" on our Discord"));
                    return true;
                }

                if (!plugin.getCache().verifyCode(uuid.toString(), strings[1])) {
                    //code incorrect
                    player.sendMessage(formatchat("&4Sorry, that code is incorrect. Please try again"));
                    return true;
                }


                if (plugin.getData().addLinkedUser(player.getName(), uuid.toString(), plugin.getCache().getUUIDDiscordID(uuid.toString()))) {
                    //Store Results
                    player.sendMessage(formatchat("&4Your account has been verified and is being linked"));
                    String discordDisplayName = plugin.getDiscord().Discord().jda.getUserById(plugin.getCache().getUUIDDiscordID(uuid.toString())).getName() + "#" + plugin.getDiscord().Discord().jda.getUserById(plugin.getCache().getUUIDDiscordID(uuid.toString())).getDiscriminator();
                    player.sendMessage(formatchat("&6Linked to: " + discordDisplayName));
                    player.sendMessage(formatchat("&6You can unlink with /discordauth unlink"));
                } else {
                    player.sendMessage(formatchat("&4An error occurred while trying to link"));
                }


                return true;


            } else if (strings[0].equalsIgnoreCase("unlink")) {
                UUID uuid = PoseidonUUID.getPlayerMojangUUID(player.getName());
                if (uuid == null) {
                    //Players UUID can't be found
                    player.sendMessage(formatchat("&4Sorry, we can't find a UUID corresponding to your account\nPlease make sure you are using a premium account\nIf the issue persists please contact staff!"));
                    return true;
                }
                if (plugin.getData().isUUIDAlreadyLinked(uuid.toString())) {
                    if (plugin.getData().removeLinkByUUID(uuid.toString())) {
                        player.sendMessage(formatchat("&6Discord link has been removed"));
                    } else {
                        player.sendMessage(formatchat("&4Sorry, We encountered a error removing the link. Please contact staff to get assistance!"));
                    }
                } else {
                    player.sendMessage(formatchat("&4Sorry, we couldn't find a linked account to this UUID!"));
                }

            } else if (strings[0].equalsIgnoreCase("status")) {
                UUID uuid = PoseidonUUID.getPlayerMojangUUID(player.getName());
                if (uuid == null) {
                    //Players UUID can't be found
                    player.sendMessage(formatchat("&4Sorry, we can't find a UUID corresponding to your account\nPlease make sure you are using a premium account\nIf the issue persists please contact staff!"));
                    return true;
                }

                if (plugin.getData().isUUIDAlreadyLinked(uuid.toString())) {
                    String discordDisplayName = plugin.getDiscord().Discord().jda.getUserById(plugin.getData().getDiscordIDFromUUID(uuid.toString())).getName() + "#" + plugin.getDiscord().Discord().jda.getUserById(plugin.getData().getDiscordIDFromUUID(uuid.toString())).getDiscriminator();
                    player.sendMessage(formatchat("&6Linked to: " + discordDisplayName));
                } else {
                    player.sendMessage(formatchat("&4Sorry, we couldn't find a linked account to this UUID!"));
                }

            }

            return true;
        }


        return false;
    }

    public static String formatchat(String msg) {
        return msg.replaceAll("(&([a-f0-9]))", "\u00A7$2");
    }
}
