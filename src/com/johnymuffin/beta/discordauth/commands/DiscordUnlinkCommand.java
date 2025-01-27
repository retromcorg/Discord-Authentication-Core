package com.johnymuffin.beta.discordauth.commands;

import com.johnymuffin.beta.discordauth.DiscordAuthentication;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DiscordUnlinkCommand extends DiscordBaseCommand {

    public DiscordUnlinkCommand(DiscordAuthentication plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Console can't run this command");
            return true;
        }

        Player player = (Player) commandSender;

        UUID uuid = player.getUniqueId();

        if(!data.isUUIDAlreadyLinked(uuid)) {
            player.sendMessage(formatchat("&4Your Minecraft account is not linked to a Discord account."));
            return true;
        }

        if(data.removeLinkByUUID(uuid)) {
            player.sendMessage(formatchat("&4Your Minecraft account has been unlinked from your Discord account."));
        } else {
            player.sendMessage(formatchat("&4An error occurred while unlinking your account. Please contact an administrator."));
        }

        return true;
    }
}
