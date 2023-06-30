package dev.slne.discord.listener.whitelist;

import java.util.List;
import java.util.Optional;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.discord.guild.DiscordGuild;
import dev.slne.discord.discord.guild.DiscordGuilds;
import dev.slne.discord.listener.Listener;
import dev.slne.discord.listener.event.EventHandler;
import dev.slne.discord.listener.event.events.BotStartEvent;
import dev.slne.discord.whitelist.Whitelist;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class WhitelistStartListener implements Listener {

    @EventHandler
    @SuppressWarnings({ "java:S3776", "java:S135" })
    public void onBotStart(BotStartEvent event) {
        for (DiscordGuild discordGuild : DiscordGuilds.getGuilds()) {
            String guildId = discordGuild.getGuildId();

            if (guildId == null) {
                return;
            }

            Guild guild = DiscordBot.getInstance().getJda().getGuildById(guildId);

            if (guild == null) {
                return;
            }

            Role whitelistedRole = discordGuild.getWhitelistedRole();

            if (whitelistedRole == null) {
                return;
            }

            Whitelist.getAllWhitelists().whenComplete(whitelistsOptional -> {
                if (whitelistsOptional.isEmpty()) {
                    return;
                }

                List<Whitelist> whitelists = whitelistsOptional.get();

                for (Whitelist whitelist : whitelists) {
                    Optional<User> whitelistedUser = whitelist.getDiscordUser();

                    if (!whitelistedUser.isPresent()) {
                        continue;
                    }

                    User user = whitelistedUser.get();

                    if (user == null) {
                        continue;
                    }

                    Member member = guild.getMember(user);

                    if (member == null) {
                        continue;
                    }

                    if (member.getRoles().contains(whitelistedRole)) {
                        continue;
                    }

                    guild.addRoleToMember(member, whitelistedRole).queue();
                }
            });
        }
    }

}
