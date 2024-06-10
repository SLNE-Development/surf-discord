package dev.slne.discord.listener.whitelist;

import dev.slne.data.api.DataApi;
import dev.slne.discord.config.discord.GuildConfig;
import dev.slne.discord.whitelist.WhitelistService;
import feign.FeignException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletionException;

/**
 * The type Whitelist join listener.
 */
public class WhitelistJoinListener extends ListenerAdapter {

	@Override
	public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
		User user = event.getUser();

		WhitelistService.INSTANCE.getWhitelistByDiscordId(user.getId()).thenAcceptAsync(whitelist -> {
			if (whitelist == null) {
				return;
			}

			Guild guild = event.getGuild();
			GuildConfig guildConfig = GuildConfig.getConfig(guild.getId());

			if (guildConfig == null) {
				return;
			}

			Role whitelistedRole = guildConfig.getWhitelistedRole();

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
			if (throwable instanceof CompletionException && throwable.getCause() instanceof FeignException.NotFound) {
				return null; // User is not whitelisted - ignore
			}

			DataApi.getDataInstance().logError(getClass(), "Failed to get whitelist by discord id.", throwable);
			return null;
		});
	}

}
