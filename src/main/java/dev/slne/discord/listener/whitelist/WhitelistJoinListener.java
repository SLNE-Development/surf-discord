package dev.slne.discord.listener.whitelist;

import javax.annotation.Nonnull;

import dev.slne.discord.discord.guild.DiscordGuild;
import dev.slne.discord.discord.guild.DiscordGuilds;
import dev.slne.discord.whitelist.Whitelist;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class WhitelistJoinListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        User user = event.getUser();
        Whitelist.getWhitelistByDiscordId(user.getId()).whenComplete(whitelistOptional -> {
            if (!whitelistOptional.isPresent()) {
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
        });
    }

}
