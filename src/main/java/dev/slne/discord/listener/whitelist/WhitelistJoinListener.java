package dev.slne.discord.listener.whitelist;

import dev.slne.data.api.DataApi;
import dev.slne.discord.discord.guild.DiscordGuild;
import dev.slne.discord.discord.guild.DiscordGuilds;
import dev.slne.discord.whitelist.Whitelist;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class WhitelistJoinListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        User user = event.getUser();

        Whitelist.getWhitelistByDiscordId(user.getId()).thenAcceptAsync(whitelist -> {
            if (whitelist == null) {
                return;
            }

            Guild guild = event.getGuild();
            DiscordGuild discordGuild = DiscordGuilds.getGuild(guild);

            if (discordGuild == null) {
                return;
            }

            Role whitelistedRole = discordGuild.getWhitelistedRole();

            if (whitelistedRole == null) {
                return;
            }

            guild.retrieveMember(user).queue(member -> {
                if (member == null) {
                    return;
                }

                guild.addRoleToMember(member, whitelistedRole).queue();
            });
        }).exceptionally(throwable -> {
            DataApi.getDataInstance().logError(getClass(), "Failed to get whitelist by discord id.", throwable);
            return null;
        });
    }

}
