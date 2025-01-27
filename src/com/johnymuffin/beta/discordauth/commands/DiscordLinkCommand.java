package com.johnymuffin.beta.discordauth.commands;

import com.johnymuffin.beta.discordauth.DiscordAuthentication;
import com.projectposeidon.api.PoseidonUUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DiscordLinkCommand extends DiscordBaseCommand {

    public DiscordLinkCommand(DiscordAuthentication plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Console can't run this command");
            return true;
        }

        if (strings.length == 0) {
            commandSender.sendMessage(formatchat("&4Please specify a code /link <code>"));
            return true;
        }

        Player player = (Player) commandSender;

        UUID uuid = player.getUniqueId();

        // Check if user is already linked
        if(data.isUUIDAlreadyLinked(uuid.toString())) {
            player.sendMessage(formatchat("&4Your Minecraft account is already linked to a Discord account."));
            player.sendMessage(formatchat("&4If you want to unlink your account, please run /unlink"));
            return true;
        }


        // Check if user is pending
        if(!cache.isUserPending(uuid.toString())) {
            player.sendMessage(formatchat("&4Discord Linking Process Not Started"));
            player.sendMessage(formatchat("&4Please start the linking process on Discord"));
            player.sendMessage(formatchat("&4Please run \"!link (username)\" on our Discord"));
            return true;
        }

        // Check if code is correct
        if(!cache.verifyCode(uuid, strings[0])) {
            player.sendMessage(formatchat("&4Linking failed. The code you entered is incorrect"));
            return true;
        }

        // Link the user as the code is correct
        long discordID = cache.getUUIDDiscordID(uuid);

        boolean linkUser = data.addLinkedUser(player.getName(), uuid, discordID);

        if(linkUser) {
            player.sendMessage(formatchat("&2You have successfully linked your account"));
            cache.removeCode(uuid, strings[0]);
        } else {
            player.sendMessage(formatchat("&4An error occurred while linking your account"));
        }

        return true;
    }
}
