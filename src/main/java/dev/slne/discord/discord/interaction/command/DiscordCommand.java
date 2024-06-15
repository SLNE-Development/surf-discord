package dev.slne.discord.discord.interaction.command;

import dev.slne.discord.config.role.RoleConfig;
import dev.slne.discord.discord.guild.permission.CommandPermission;
import lombok.Getter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The type Discord command.
 */
@Getter
public abstract class DiscordCommand {

	@Nonnull
	private final String name;
	@Nonnull
	private final DefaultMemberPermissions defaultMemberPermissions;
	@Nonnull
	private final String description;
	private final boolean guildOnly;
	private final boolean nsfw;

	private final SlashCommandData commandData;

	/**
	 * Creates a new DiscordCommand.
	 *
	 * @param name        The name of the command.
	 * @param description The description of the command.
	 */
	protected DiscordCommand(@Nonnull String name, @Nonnull String description) {
		this.name = name;
		this.description = description;

		this.guildOnly = true;
		this.nsfw = false;

		this.defaultMemberPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR);
		this.commandData = Commands.slash(name, description);
	}

	/**
	 * Returns the subcommands of the command.
	 *
	 * @return The subcommands of the command.
	 */
	public abstract @Nonnull List<SubcommandData> getSubCommands();

	/**
	 * Returns the options of the command.
	 *
	 * @return The options of the command.
	 */
	public abstract @Nonnull List<OptionData> getOptions();

	/**
	 * Returns the permission needed to run this command
	 *
	 * @return the permission
	 */
	public abstract @Nonnull CommandPermission getPermission();

	/**
	 * Executes the command internally
	 *
	 * @param interaction the interaction event
	 */
	public void internalExecute(SlashCommandInteractionEvent interaction) {
		User user = interaction.getUser();
		Guild guild = interaction.getGuild();

		performDiscordCommandChecks(user, guild, interaction).thenAcceptAsync(success -> {
			if (!success) {
				return;
			}

			execute(interaction);
		}).exceptionally(throwable -> {
			throwable.printStackTrace();
			return null;
		});
	}

	/**
	 * Performs the checks for the command.
	 *
	 * @param user        The user.
	 * @param guild       The guild.
	 * @param interaction The interaction.
	 *
	 * @return Whether the checks were successful.
	 */
	protected CompletableFuture<Boolean> performDiscordCommandChecks(
			User user, Guild guild, SlashCommandInteractionEvent interaction
	) {
		CompletableFuture<Boolean> future = new CompletableFuture<>();

		if (guild == null) {
			interaction.reply("Es ist ein Fehler aufgetreten (dhwfm4nD)").setEphemeral(true).queue();

			future.completeExceptionally(new IllegalStateException("Guild is null"));
			return future;
		}

		if (user == null) {
			interaction.reply("Es ist ein Fehler aufgetreten (dhwfm4nD)").setEphemeral(true).queue();

			future.completeExceptionally(new IllegalStateException("User is null"));
			return future;
		}

		guild.retrieveMember(user).submit().thenAcceptAsync(member -> {
			if (member == null) {
				interaction.reply("Es ist ein Fehler aufgetreten (9348934dwjkdjw)").setEphemeral(true).queue();

				future.completeExceptionally(new IllegalStateException("Member is null"));
				return;
			}

			List<Role> userDiscordRoles = member.getRoles();
			List<RoleConfig> userRoles = userDiscordRoles.stream()
														 .map(role -> RoleConfig.getDiscordRoleRoles(
																 guild.getId(),
																 role.getId()
														 ))
														 .flatMap(List::stream)
														 .toList();

			boolean hasPermission = false;

			for (RoleConfig userRole : userRoles) {
				if (userRole.hasCommandPermission(getPermission())) {
					hasPermission = true;
					break;
				}
			}

			if (!hasPermission) {
				interaction.reply("Du besitzt keine Berechtigung diesen Befehl zu verwenden.")
						   .setEphemeral(true).queue();

				future.complete(false);
				return;
			}

			future.complete(true);
		});

		return future;
	}

	/**
	 * Executes the command.
	 *
	 * @param interaction The interaction.
	 */
	public abstract void execute(SlashCommandInteractionEvent interaction);

}
