package dev.slne.discord.discord.guild;

import dev.slne.data.api.DataApi;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.discord.guild.reactionrole.ReactionRoleConfig;
import dev.slne.discord.discord.guild.role.DiscordRole;
import dev.slne.discord.ticket.TicketType;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The type Discord guild.
 */
@Getter
public class DiscordGuild {

	@NotNull
	private final @Nonnull String guildId;
	@NotNull
	private final @Nonnull String categoryId;

	@Nonnull
	private final String whitelistedRoleId;
	private final Role whitelistedRole;

	@SuppressWarnings("unused")
	private final String discordSupportAdminRoleId;
	@SuppressWarnings("unused")
	private final String serverSupportAdminRoleId;
	@SuppressWarnings("unused")
	private final String discordSupportModeratorRoleId;
	@SuppressWarnings("unused")
	private final String serverSupportModeratorRoleId;

	private Role discordSupportAdminRole;
	private Role serverSupportAdminRole;
	private Role discordSupportModeratorRole;
	private Role serverSupportModeratorRole;

	/**
	 * -- SETTER --
	 * Sets the reaction role config.
	 *
	 * @param reactionRoleConfig the rrConfig to set
	 */
	@Setter
	private ReactionRoleConfig reactionRoleConfig;

	/**
	 * Construct a new DiscordGuild.
	 *
	 * @param guildId                     The guild id.
	 * @param categoryId                  The category id .
	 * @param discordSupportAdminRole     The discord support admins.
	 * @param serverSupportAdminRole      The server support admins.
	 * @param discordSupportModeratorRole The discord support moderators.
	 * @param serverSupportModeratorRole  The server support moderators.
	 * @param whitelistedRoleId           The whitelisted role id.
	 * @param rrConfig                    The reaction role config.
	 */
	public DiscordGuild(
			@Nonnull String guildId, @Nonnull String categoryId, String discordSupportAdminRole,
			String serverSupportAdminRole, String discordSupportModeratorRole,
			String serverSupportModeratorRole, @Nonnull String whitelistedRoleId,
			ReactionRoleConfig rrConfig
	) {
		this.guildId = guildId;
		this.categoryId = categoryId;

		this.discordSupportAdminRoleId = discordSupportAdminRole;
		this.serverSupportAdminRoleId = serverSupportAdminRole;
		this.discordSupportModeratorRoleId = discordSupportModeratorRole;
		this.serverSupportModeratorRoleId = serverSupportModeratorRole;

		if (discordSupportAdminRoleId != null) {
			this.discordSupportAdminRole = DiscordBot.getInstance().getJda().getRoleById(discordSupportAdminRoleId);
		}

		if (serverSupportAdminRoleId != null) {
			this.serverSupportAdminRole = DiscordBot.getInstance().getJda().getRoleById(serverSupportAdminRoleId);
		}

		if (discordSupportModeratorRoleId != null) {
			this.discordSupportModeratorRole =
					DiscordBot.getInstance().getJda().getRoleById(discordSupportModeratorRoleId);
		}

		if (serverSupportModeratorRoleId != null) {
			this.serverSupportModeratorRole =
					DiscordBot.getInstance().getJda().getRoleById(serverSupportModeratorRoleId);
		}

		this.whitelistedRoleId = whitelistedRoleId;
		this.whitelistedRole = DiscordBot.getInstance().getJda().getRoleById(whitelistedRoleId);

		this.reactionRoleConfig = rrConfig;
	}

	/**
	 * Returns if the user has the given role
	 *
	 * @param role The role
	 * @param user The user
	 *
	 * @return if the user has the given role
	 */
	private CompletableFuture<Boolean> roleHasUser(Role role, User user) {
		CompletableFuture<Boolean> future = new CompletableFuture<>();

		CompletableFuture.runAsync(() -> {
			if (role == null) {
				future.complete(false);
				return;
			}

			role.getGuild().retrieveMember(user)
				.queue(
						member -> future.complete(member != null && member.getRoles().contains(role)),
						failure -> DataApi.getDataInstance()
										  .logError(getClass(), "Failed to retrieve member", failure)
				);
		});

		return future;
	}

	/**
	 * Returns if the user is a support admin.
	 *
	 * @param user The user.
	 *
	 * @return If the user is a support admin.
	 */
	public CompletableFuture<Boolean> isAdminUser(User user) {
		CompletableFuture<Boolean> discordAdminFuture = roleHasUser(discordSupportAdminRole, user);
		CompletableFuture<Boolean> serverAdminFuture = roleHasUser(serverSupportAdminRole, user);
		CompletableFuture<Boolean> discordModFuture = roleHasUser(discordSupportModeratorRole, user);
		CompletableFuture<Boolean> serverModFuture = roleHasUser(serverSupportModeratorRole, user);

		return CompletableFuture.allOf(discordAdminFuture, serverAdminFuture, discordModFuture, serverModFuture)
								.thenApplyAsync(
										unused -> discordAdminFuture.join() || serverAdminFuture.join() ||
												  discordModFuture.join()
												  || serverModFuture.join());
	}

	/**
	 * Returns the discord role by the role.
	 *
	 * @param role The role.
	 *
	 * @return The discord role.
	 */
	public DiscordRole getDiscordRoleByRole(Role role) {
		if (role == null) {
			return null;
		}

		if (role.getId().equals(discordSupportAdminRoleId)) {
			return DiscordBot.getInstance().getRoleManager()
							 .getRoleByName(DiscordRole.DISCORD_ADMIN_ROLE);
		}

		if (role.getId().equals(discordSupportModeratorRoleId)) {
			return DiscordBot.getInstance().getRoleManager()
							 .getRoleByName(DiscordRole.DISCORD_MOD_ROLE);
		}

		if (role.getId().equals(serverSupportAdminRoleId)) {
			return DiscordBot.getInstance().getRoleManager()
							 .getRoleByName(DiscordRole.SERVER_ADMIN_ROLE);
		}

		if (role.getId().equals(serverSupportModeratorRoleId)) {
			return DiscordBot.getInstance().getRoleManager()
							 .getRoleByName(DiscordRole.SERVER_MOD_ROLE);
		}

		return DiscordBot.getInstance().getRoleManager()
						 .getRoleByName(DiscordRole.DEFAULT_ROLE);
	}

	/**
	 * Returns if the role can view the ticket channel.
	 *
	 * @param role       The role.
	 * @param ticketType The ticket type.
	 *
	 * @return If the role can view the ticket channel.
	 */
	public boolean canRoleViewTicket(Role role, TicketType ticketType) {
		DiscordRole discordRole = getDiscordRoleByRole(role);

		if (discordRole == null) {
			return false;
		}

		return discordRole.canViewTicketChannel(ticketType);
	}

	/**
	 * Returns the guild roles of a user.
	 *
	 * @param userId The userid.
	 *
	 * @return The guild roles.
	 */
	public CompletableFuture<List<DiscordRole>> getGuildRoles(String userId) {
		CompletableFuture<List<DiscordRole>> roles = new CompletableFuture<>();

		DiscordBot.getInstance().getJda().retrieveUserById(userId)
				  .queue(user -> getGuildRoles(user).thenAcceptAsync(roles::complete).exceptionally(throwable -> {
					  roles.completeExceptionally(throwable);
					  return null;
				  }), roles::completeExceptionally);

		return roles;
	}

	/**
	 * Returns the guild roles of a user.
	 *
	 * @param user The user.
	 *
	 * @return The guild roles.
	 */
	public CompletableFuture<List<DiscordRole>> getGuildRoles(User user) {
		CompletableFuture<List<DiscordRole>> roles = new CompletableFuture<>();
		List<DiscordRole> roleList = new ArrayList<>();

		DiscordRole discordAdminRole = DiscordBot.getInstance().getRoleManager()
												 .getRoleByName(DiscordRole.DISCORD_ADMIN_ROLE);
		DiscordRole discordModRole = DiscordBot.getInstance().getRoleManager()
											   .getRoleByName(DiscordRole.DISCORD_MOD_ROLE);
		DiscordRole serverAdminRole = DiscordBot.getInstance().getRoleManager()
												.getRoleByName(DiscordRole.SERVER_ADMIN_ROLE);
		DiscordRole serverModRole = DiscordBot.getInstance().getRoleManager()
											  .getRoleByName(DiscordRole.SERVER_MOD_ROLE);

		DiscordRole defaultRole = DiscordBot.getInstance().getRoleManager()
											.getRoleByName(DiscordRole.DEFAULT_ROLE);

		if (user == null) {
			roles.completeExceptionally(new NullPointerException("User is null"));
		}

		CompletableFuture<Boolean> discordAdminFuture = roleHasUser(discordSupportAdminRole, user);
		CompletableFuture<Boolean> discordModFuture = roleHasUser(discordSupportModeratorRole, user);
		CompletableFuture<Boolean> serverAdminFuture = roleHasUser(serverSupportAdminRole, user);
		CompletableFuture<Boolean> serverModFuture = roleHasUser(serverSupportModeratorRole, user);

		CompletableFuture.allOf(discordAdminFuture, discordModFuture, serverAdminFuture, serverModFuture)
						 .thenAcceptAsync(unused -> {
							 if (discordAdminFuture.join()) {
								 roleList.add(discordAdminRole);
							 }

							 if (discordModFuture.join()) {
								 roleList.add(discordModRole);
							 }

							 if (serverAdminFuture.join()) {
								 roleList.add(serverAdminRole);
							 }

							 if (serverModFuture.join()) {
								 roleList.add(serverModRole);
							 }

							 if (roleList.isEmpty() && defaultRole != null) {
								 roleList.add(defaultRole);
							 }

							 roles.complete(roleList);
						 });

		return roles;
	}

}
