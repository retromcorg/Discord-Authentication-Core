package com.johnymuffin.beta.discordauth;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class DiscordAuthListener extends PlayerListener {
    private DiscordAuthentication plugin;

    public DiscordAuthListener(DiscordAuthentication plugin) {
        this.plugin = plugin;

    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getData().updateLastKnownUsername(event.getPlayer().getUniqueId(), event.getPlayer().getName());

        final UUID uuid = event.getPlayer().getUniqueId();

        if (this.plugin.getConfig().getConfigBoolean("settings.discord.automatic-nickname.enabled")) {
            //Check if the player has a linked account
            if (!this.plugin.getData().isUUIDAlreadyLinked(uuid.toString())) {
                return;
            }
            final String playerUsername = event.getPlayer().getName();
            final String discordID = this.plugin.getData().getDiscordIDFromUUID(event.getPlayer().getUniqueId().toString());
            final List<Object> guilds = this.plugin.getConfig().getAutomaticNicknameGuilds();
            Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
                //Loop through all guilds
                for (Object guildIDObject : guilds) {
                    String guildID = String.valueOf(guildIDObject);
                    if (guildID.equalsIgnoreCase("0")) {
                        continue;
                    }
                    User user = plugin.getDiscord().getDiscordBot().getJda().getUserById(discordID);

                    if (user == null) {
                        plugin.logger(Level.WARNING, "User is null, they might not share a server with the bot");
                        return;
                    }

                    //Check if the user is in the guild
                    boolean isMember = false;
                    Guild guild2 = null;
                    for (Guild guild : plugin.getDiscord().getDiscordBot().getJda().getGuilds()) {
                        if (guild.getId().equalsIgnoreCase(guildID)) {
                            isMember = true;
                            guild2 = guild;
                            break;
                        }
                    }

                    if (!isMember) {
                        plugin.logger(Level.WARNING, "the bot is not a member of guild " + guildID + " and thus unable to update the nickname for " + playerUsername);
                        continue;
                    }

                    if (!guild2.isMember(user)) {
                        plugin.logger(Level.WARNING, "the user " + playerUsername + " is not a member of guild " + guildID + " and thus unable to update the nickname for " + playerUsername);
                        continue;
                    }

                    String guildName = guild2.getName();

                    Member member = plugin.getDiscord().getDiscordBot().getJda().getGuildById(guildID).getMember(user);
                    try {
//                            Member member = plugin.getDiscord().getDiscordBot().getJda().getGuildById(guildID).getMember(user);
                        plugin.getDiscord().getDiscordBot().getJda().getGuildById(guildID).modifyNickname(member, playerUsername).queue();
                        plugin.logInfo("Updated nickname for " + playerUsername + " with Discord name " + user.getName() + " in guild " + guildName);
                    } catch (HierarchyException exception) {
                        plugin.logger(Level.WARNING, "Could not update nickname for " + playerUsername + " in guild " + guildName + ". The user has a higher role than the bot");
                    } catch (InsufficientPermissionException exception) {
                        plugin.logger(Level.WARNING, "Could not update nickname for " + playerUsername + " in guild " + guildName + ". The bot does not have permission to change their nickname");
                    } catch (Exception exception) {
                        plugin.logger(Level.WARNING, "Could not update nickname for " + playerUsername + " in guild " + guildName + ". An unknown error occurred");
                        exception.printStackTrace();
                    }

                }
            }, 0L);


        }
    }

}
