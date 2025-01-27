package com.johnymuffin.beta.discordauth.discordcommands;

import com.johnymuffin.beta.discordauth.DiscordAuthentication;
import com.projectposeidon.api.PoseidonUUID;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.UUID;
import java.util.logging.Level;

import static com.johnymuffin.beta.discordauth.Utilities.generateCode;

public class DiscordAuthListener extends ListenerAdapter {
    private DiscordAuthentication plugin;


    public DiscordAuthListener(DiscordAuthentication plugin) {
        this.plugin = plugin;

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.isWebhookMessage()) return;
        String[] ags = event.getMessage().getContentRaw().split(" ");
        if (ags[0].toLowerCase().equals("!link")) {
            plugin.logger(Level.INFO, "User: " + event.getAuthor().getName() + " | Command: " + event.getMessage().getContentRaw());

            if (ags.length != 2) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.RED);
                embed.setTitle("Incorrect Usage");
                embed.setDescription("To link your account, use the command:\n\n**`!link <username>`**");
                embed.addField("Example", "`!link JohnDoe`", false);
                embed.setFooter("Ensure you replace <username> with your actual username.");

                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            }
            //Start Verification
            Boolean found = false;
            Player p = null;
            if (plugin.getData().isDiscordIDAlreadyLinked(event.getAuthor().getId())) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.RED);
                embed.setTitle("Account Already Linked");
                embed.setDescription("This account has already been linked to a Minecraft account.\n\nTo unlink, use the command:\n\n**`!unlink`**");

                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            }

            for (Player p2 : Bukkit.getServer().getOnlinePlayers()) {
                if (ags[1].equalsIgnoreCase(p2.getName())) {
                    //Player is online
                    found = true;
                    p = p2;
                    break;
                }
            }
            if (!found) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.RED);
                embed.setTitle("Player Not Found");
                embed.setDescription("Could not find a player online with that username.\n\nPlease ensure you are online and have specified the correct name.");

                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            }
            //Check we have a UUID in Storage
            UUID uuid = plugin.getPlayerUUID(p.getName());

            if (uuid == null) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.RED);
                embed.setTitle("UUID Not Found");
                embed.setDescription("Sorry, we couldn't find a UUID linked to that username.\n\nPlease ensure you are using a Premium account or try again later. If the issue persists please contact staff.");

                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            }


            if (plugin.getData().isUUIDAlreadyLinked(uuid.toString())) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.RED);
                embed.setTitle("Account Already Linked");
                embed.setDescription("This account has already been linked to a Discord account.\n\nTo unlink, use the command:\n\n**`!unlink`**");

                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            }

            String securityCode = generateCode(4);

            EmbedBuilder linkingCodeEmbed = new EmbedBuilder();
            linkingCodeEmbed.setColor(Color.GREEN);
            linkingCodeEmbed.setTitle("Verification Code");
            linkingCodeEmbed.setDescription("You have started the linking process.\n\nPlease run the command `/link " + securityCode + "` in-game.");
            linkingCodeEmbed.setFooter("This code will expire in 5 minutes. If you encounter any issues, please contact staff.");


            if (plugin.getConfig().getConfigBoolean("settings.discord.send-linking-code-via-dm.enabled")) {

                // Tell user to check DMs for the linking code
                EmbedBuilder checkDMEmbed = new EmbedBuilder();
                checkDMEmbed.setColor(Color.GREEN);
                checkDMEmbed.setTitle("Verification Started");
                checkDMEmbed.setDescription("We have started the verification process for your account.\n\nPlease check your Direct Messages for further instructions.");
                checkDMEmbed.setFooter("If you do not receive a message, please ensure you have Direct Messages enabled for this server.");
                event.getChannel().sendMessageEmbeds(checkDMEmbed.build()).queue();

                // Send the linking code via DM. If failure tell user to enable DMs

                event.getAuthor().openPrivateChannel().flatMap(channel -> channel.sendMessageEmbeds(linkingCodeEmbed.build())).queue(null, new ErrorHandler().handle(ErrorResponse.CANNOT_SEND_TO_USER, (error) -> {
                    plugin.logger(Level.WARNING, "Error sending DM to " + event.getAuthor().getName() + " (" + event.getAuthor().getId() + "). This user may have DMs disabled.");
                    EmbedBuilder errorEmbed = new EmbedBuilder();
                    errorEmbed.setColor(Color.RED);
                    errorEmbed.setTitle("Error Sending DM");
                    errorEmbed.setDescription("We were unable to send you a Direct Message with the linking code.\n\nPlease ensure you have Direct Messages enabled for this server.");
                    event.getChannel().sendMessageEmbeds(errorEmbed.build()).queue();
                }));
            } else {
                // Send the linking code in the channel
                event.getChannel().sendMessageEmbeds(linkingCodeEmbed.build()).queue();
            }

            // Add code to cache
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                plugin.logger(Level.INFO, "Adding code (" + securityCode + ") to cache for UUID: " + uuid.toString());
                plugin.getCache().addCodeToken(uuid.toString(), securityCode, event.getAuthor().getId());
            }, 0L);

            // Remove code from cache after 5 minutes
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                plugin.logger(Level.INFO, "Removing code (" + securityCode + ") from cache for UUID: " + uuid.toString());
                plugin.getCache().removeCode(uuid, securityCode);
            }, 6000L);


        } else if (ags[0].toLowerCase().equals("!unlink")) {
            plugin.logger(Level.INFO, "User: " + event.getAuthor().getName() + " | Command: " + event.getMessage().getContentRaw());

            if (plugin.getData().isDiscordIDAlreadyLinked(event.getAuthor().getId())) {
                if (plugin.getData().removeLinkFromDiscordID(event.getAuthor().getId())) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(Color.GREEN);
                    embed.setTitle("Account Unlinked");
                    embed.setDescription("This Discord account has been unlinked from any Minecraft account.");

                    event.getChannel().sendMessageEmbeds(embed.build()).queue();

                } else {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(Color.RED);
                    embed.setTitle("Error Unlinking");
                    embed.setDescription("An error occurred while trying to unlink your account.\n\nPlease contact staff for assistance.");

                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                }
                return;
            } else {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.RED);
                embed.setTitle("Account Not Linked");
                embed.setDescription("This Discord account is currently not linked to any Minecraft account.");

                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            }

        } else if (ags[0].toLowerCase().equals("!status")) {
            plugin.logger(Level.INFO, "User: " + event.getAuthor().getName() + " | Command: " + event.getMessage().getContentRaw());

            if (plugin.getData().isDiscordIDAlreadyLinked(event.getAuthor().getId())) {

                String uuid = plugin.getData().getUUIDFromDiscordID(event.getAuthor().getId());
                String message = "We found the link details below";
                message = message + "\nUUID: " + uuid;
                String username = PoseidonUUID.getPlayerUsernameFromUUID(UUID.fromString(uuid));
                if (username == null) username = "Unknown User";
                message = message + "\nCurrent Username: " + username;
                String finalMessage = message;

                event.getChannel().sendMessage(finalMessage).queue();


            } else {
                event.getChannel().sendMessage("This Discord account is currently not linked to any account").queue();
            }

        }
    }
}
