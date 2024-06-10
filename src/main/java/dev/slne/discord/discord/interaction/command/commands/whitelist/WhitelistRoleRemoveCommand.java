package dev.slne.discord.discord.interaction.command.commands.whitelist;

import dev.slne.discord.config.discord.GuildConfig;
import dev.slne.discord.discord.guild.permission.CommandPermission;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The type Whitelist role remove command.
 */
public class WhitelistRoleRemoveCommand extends DiscordCommand {

	/**
	 * Creates a new WhitelistRoleRemoveCommand.
	 */
	public WhitelistRoleRemoveCommand() {
		super("wlrole", "Entfernt alle Benutzer aus der Whitelist Rolle.");
	}

	@Override
	public @Nonnull List<SubcommandData> getSubCommands() {
		return new ArrayList<>();
	}

	@Override
	public @Nonnull List<OptionData> getOptions() {
		return new ArrayList<>();
	}

	@Override
	public @Nonnull CommandPermission getPermission() {
		return CommandPermission.WHITELIST_ROLE;
	}

	@Override
	public void execute(SlashCommandInteractionEvent interaction) {
		interaction.deferReply(true).queue(hook -> {
			Guild guild = interaction.getGuild();

			if (guild == null) {
				hook.editOriginal("Du musst auf einem Server sein, um diesen Command auszuführen.").queue();
				return;
			}

			GuildConfig guildConfig = GuildConfig.getConfig(guild.getId());

			if (guildConfig == null) {
				hook.editOriginal("Dieser Server ist nicht registriert.").queue();
				return;
			}

			Role whitelistedRole = guild.getRoleById(guildConfig.getWhitelistRoleId());

			if (whitelistedRole == null) {
				hook.editOriginal("Die Whitelist Rolle ist nicht registriert.").queue();
				return;
			}

			List<CompletableFuture<?>> futures = new ArrayList<>();

			guild.findMembersWithRoles(whitelistedRole).onSuccess(members -> {
				members.forEach(member -> {
					if (member == null) {
						return;
					}

					futures.add(guild.removeRoleFromMember(member, whitelistedRole).submit());
				});

				CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
								 .thenRun(() -> hook
										 .editOriginal(
												 futures.size() + " Benutzer wurden aus der Whitelist Rolle entfernt.")
										 .queue());
			}).onError(Throwable::printStackTrace);
		});
	}

}
