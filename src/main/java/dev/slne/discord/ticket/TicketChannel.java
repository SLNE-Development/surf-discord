package dev.slne.discord.ticket;

import dev.slne.data.api.DataApi;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.config.BotConfig;
import dev.slne.discord.config.role.RoleConfig;
import dev.slne.discord.discord.guild.permission.DiscordPermission;
import dev.slne.discord.ticket.TicketPermissionOverride.Type;
import dev.slne.discord.ticket.member.TicketMember;
import dev.slne.discord.ticket.result.TicketCreateResult;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import space.arim.dazzleconf.annote.SubSection;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * The type Ticket channel.
 */
public class TicketChannel {

	/**
	 * Private constructor to prevent instantiation
	 */
	private TicketChannel() {
	}

	/**
	 * Adds a ticket member to the channel
	 *
	 * @param ticket       the ticket
	 * @param ticketMember the ticket member
	 *
	 * @return when completed
	 */
	public static CompletableFuture<Void> addTicketMember(Ticket ticket, TicketMember ticketMember) {
		CompletableFuture<Void> future = new CompletableFuture<>();

		Guild guild = ticket.getGuild();
		TextChannel channel = ticket.getChannel();
		RestAction<User> userRest = ticketMember.getMember();

		if (guild == null || channel == null || userRest == null) {
			future.complete(null);
			return future;
		}

		TextChannelManager manager = channel.getManager();

		userRest.queue(user -> {
			if (user == null) {
				future.completeExceptionally(new RuntimeException("User not found"));
				return;
			}

			Member member = guild.getMember(user);

			if (member == null) {
				future.completeExceptionally(new RuntimeException("Member not found"));
				return;
			}

			List<Role> userDiscordRoles = member.getRoles();
			List<RoleConfig> userRoles = userDiscordRoles.stream()
														 .map(role -> RoleConfig.getDiscordRoleRoles(role.getId()))
														 .flatMap(List::stream)
														 .toList();
			List<Permission> permissions = new ArrayList<>();

			for (RoleConfig role : userRoles) {
				for (DiscordPermission discordPermission : role.getDiscordAllowedPermissions()) {
					Permission permission = discordPermission.getPermission();

					if (!permissionAdded(permissions, permission)) {
						permissions.add(permission);
					}
				}
			}

			manager.putMemberPermissionOverride(user.getIdLong(), permissions, new ArrayList<>())
				   .queue(future::complete, future::completeExceptionally);
		}, future::completeExceptionally);

		return future;
	}

	/**
	 * Removes a ticket member from the channel
	 *
	 * @param ticket       the ticket
	 * @param ticketMember the ticket member
	 *
	 * @return when completed
	 */
	public static CompletableFuture<Void> removeTicketMember(Ticket ticket, TicketMember ticketMember) {
		CompletableFuture<Void> future = new CompletableFuture<>();

		TextChannel channel = ticket.getChannel();
		RestAction<User> userRest = ticketMember.getMember();

		if (channel == null || userRest == null) {
			future.complete(null);
			return future;
		}

		TextChannelManager manager = channel.getManager();

		userRest.queue(user -> {
			if (user == null) {
				future.completeExceptionally(new RuntimeException("User not found"));
				return;
			}

			manager.removePermissionOverride(user.getIdLong()).queue(future::complete, future::completeExceptionally);
		}, future::completeExceptionally);

		return future;
	}

	/**
	 * Returns the default permission overrides for the ticket channel
	 *
	 * @param ticket The ticket
	 * @param author The author of the ticket
	 *
	 * @return The default permission overrides for the ticket channel
	 */
	public static List<TicketPermissionOverride> getChannelPermissions(Ticket ticket, User author) {
		Guild guild = ticket.getGuild();
		TicketType ticketType = ticket.getTicketType();

		List<Permission> allPermissions = getAllPermissions();
		List<TicketPermissionOverride> overrides = new ArrayList<>();

		// Deny public role
		overrides.add(new TicketPermissionOverride(Type.ROLE, guild.getPublicRole().getIdLong(), new ArrayList<>(),
												   allPermissions
		));

		// Allow bot user
		overrides.add(
				new TicketPermissionOverride(Type.USER, DiscordBot.getInstance().getJda().getSelfUser().getIdLong(),
											 allPermissions, new ArrayList<>()
				));

		// Allow bot role
		Role botRole = guild.getBotRole();
		if (botRole != null) {
			overrides.add(new TicketPermissionOverride(Type.ROLE, botRole.getIdLong(), allPermissions,
													   new ArrayList<>()
			));
		}

		Map<String, @SubSection RoleConfig> roleConfig = BotConfig.getConfig().getRoleConfig();
		for (Map.Entry<String, RoleConfig> entry : roleConfig.entrySet()) {
			RoleConfig config = entry.getValue();

			if (config == null) {
				continue;
			}

			if (RoleConfig.getDefaultRole() == null) {
				continue;
			}

			if (!config.canViewTicketType(ticketType)) {
				for (String roleId : config.getDiscordRoleIds()) {
					Role role = guild.getRoleById(roleId);
					if (role != null) {
						overrides.add(new TicketPermissionOverride(Type.ROLE, role.getIdLong(), new ArrayList<>(),
																   allPermissions
						));
					}
				}
			} else {
				for (String roleId : config.getDiscordRoleIds()) {
					Role role = guild.getRoleById(roleId);
					if (role != null) {
						overrides.add(new TicketPermissionOverride(Type.ROLE, role.getIdLong(), allPermissions,
																   new ArrayList<>()
						));
					}
				}
			}
		}

		RoleConfig defaultRole = RoleConfig.getDefaultRole();

		// Apply author
		overrides.add(
				new TicketPermissionOverride(
						Type.USER, author.getIdLong(),
						defaultRole.getDiscordAllowedPermissions().stream().map(DiscordPermission::getPermission)
								   .toList(),
						new ArrayList<>()
				));

		return overrides;
	}

	/**
	 * Creates the author ticket member
	 *
	 * @param ticket        The ticket
	 * @param author        The author of the ticket
	 * @param channelAction the channel action
	 *
	 * @return The result of the ticket member creation
	 */
	private static CompletableFuture<TicketMember> createAuthorTicketMember(
			Ticket ticket, User author,
			ChannelAction<TextChannel> channelAction
	) {
		TicketMember ticketMember = new TicketMember(ticket, author, DiscordBot.getInstance().getJda().getSelfUser());
		RoleConfig defaultRole = RoleConfig.getDefaultRole();
		TicketPermissionOverride override = new TicketPermissionOverride(Type.USER, author.getIdLong(),
																		 defaultRole.getDiscordAllowedPermissions()
																					.stream().map(
																							DiscordPermission::getPermission)
																					.toList(),
																		 new ArrayList<>()
		);

		//noinspection ResultOfMethodCallIgnored
		channelAction.addMemberPermissionOverride(author.getIdLong(), override.allow(), override.deny());

		return ticket.addTicketMember(ticketMember);
	}

	/**
	 * Returns all permissions
	 *
	 * @return all permissions
	 */
	private static List<Permission> getAllPermissions() {
		List<Permission> allPermissions = new ArrayList<>();

		for (Permission perm : Permission.values()) {
			if (perm.isText()) {
				allPermissions.add(perm);
			}
		}

		allPermissions.add(Permission.VIEW_CHANNEL);
		allPermissions.add(Permission.MANAGE_WEBHOOKS);
		allPermissions.add(Permission.MANAGE_CHANNEL);

		return allPermissions;
	}

	/**
	 * Returns if the permissions list contains the given permission
	 *
	 * @param permissions The permissions to check in
	 * @param permission  The permission to check
	 *
	 * @return If the permissions list contains the given permission
	 */
	private static boolean permissionAdded(Collection<Permission> permissions, Permission permission) {
		boolean added = false;

		for (Permission perm : permissions) {
			if (perm == permission) {
				added = true;
				break;
			}
		}

		return added;
	}

	/**
	 * Create the ticket channel
	 *
	 * @param ticket          The ticket to create the channel for
	 * @param ticketName      The name of the ticket
	 * @param channelCategory The category to create the ticket in
	 *
	 * @return The result of the ticket creation
	 */
	public static CompletableFuture<TicketCreateResult> createTicketChannel(
			Ticket ticket, @Nonnull String ticketName,
			@Nonnull Category channelCategory
	) {
		CompletableFuture<TicketCreateResult> future = new CompletableFuture<>();
		Guild guild = ticket.getGuild();

		if (guild == null) {
			future.complete(TicketCreateResult.GUILD_NOT_FOUND);
			return future;
		}

		CompletableFuture.runAsync(() -> {
			try {
				ChannelAction<TextChannel> channelAction = channelCategory.createTextChannel(ticketName);

				ticket.getTicketAuthor()
					  .queue(author -> createAuthorTicketMember(ticket, author, channelAction).thenAcceptAsync(v -> {
						  List<TicketPermissionOverride> overrides = getChannelPermissions(ticket, author);
						  for (TicketPermissionOverride override : overrides) {
							  if (override.type() == Type.ROLE) {
								  //noinspection ResultOfMethodCallIgnored
								  channelAction.addRolePermissionOverride(override.id(),
																		  override.allow(), override.deny()
								  );
							  } else if (override.type() == Type.USER) {
								  //noinspection ResultOfMethodCallIgnored
								  channelAction.addMemberPermissionOverride(override.id(),
																			override.allow(), override.deny()
								  );
							  }
						  }

						  channelAction.queue(ticketChannel -> {
							  ticket.setChannelId(ticketChannel.getId());
							  CompletableFuture<Void> webhookFuture = createWebhook(ticket);
							  CompletableFuture<Ticket> updateFuture =
									  TicketService.INSTANCE.updateTicket(ticket);

							  webhookFuture.thenAcceptAsync(v1 -> {
							  }).exceptionally(throwable -> {
								  future.complete(TicketCreateResult.ERROR);
								  DataApi.getDataInstance()
										 .logError(TicketChannel.class, "Failed to create webhook.", throwable);
								  return null;
							  });

							  updateFuture.thenAcceptAsync(v1 -> {
							  }).exceptionally(throwable -> {
								  future.complete(TicketCreateResult.ERROR);
								  DataApi.getDataInstance()
										 .logError(TicketChannel.class, "Failed to update ticket.", throwable);
								  return null;
							  });

							  CompletableFuture
									  .allOf(webhookFuture, updateFuture)
									  .thenAccept(v1 -> future.complete(TicketCreateResult.SUCCESS));
						  }, exception -> handleException(exception, future));
					  }).exceptionally(throwable -> {
						  future.complete(TicketCreateResult.ERROR);
						  DataApi.getDataInstance()
								 .logError(TicketChannel.class, "Failed to create ticket channel.", throwable);
						  return null;
					  }), failure -> {
						  future.complete(TicketCreateResult.ERROR);
						  DataApi.getDataInstance()
								 .logError(TicketChannel.class, "Failed to create ticket channel.", failure);
					  });
			} catch (Exception exception) {
				handleException(exception, future);
			}
		}).exceptionally(throwable -> {
			handleException(throwable, future);
			return null;
		});

		return future;
	}

	/**
	 * Handles the exception
	 *
	 * @param throwable The exception
	 * @param future    The future
	 */
	private static void handleException(Throwable throwable, CompletableFuture<TicketCreateResult> future) {
		if (throwable instanceof ErrorResponseException errorResponseException
			&& errorResponseException.getErrorCode() == 50013) {
			future.complete(TicketCreateResult.MISSING_PERMISSIONS);
			return;
		} else if (throwable instanceof InsufficientPermissionException) {
			future.complete(TicketCreateResult.MISSING_PERMISSIONS);
			return;
		}

		future.complete(TicketCreateResult.ERROR);
		DataApi.getDataInstance().logError(TicketChannel.class, "Failed to create ticket channel.", throwable);
	}

	/**
	 * Get the name for the ticket channel
	 *
	 * @param ticket The ticket to get the name for
	 *
	 * @return The name for the ticket channel
	 */
	public static CompletableFuture<String> getTicketName(Ticket ticket) {
		CompletableFuture<String> future = new CompletableFuture<>();

		TicketType ticketType = ticket.getTicketType();
		ticket.getTicketAuthor().queue(ticketAuthor -> {
			if (ticketType == null || ticketAuthor == null) {
				future.complete(null);
				return;
			}

			String ticketTypeName = ticketType.name().toLowerCase();
			String authorName = ticketAuthor.getName().toLowerCase().trim().replace(" ", "-");

			int maxLength = Channel.MAX_NAME_LENGTH;
			String ticketName = ticketTypeName + "-" + authorName;

			if (ticketName.length() > maxLength) {
				ticketName = ticketName.substring(0, maxLength);
			}

			future.complete(ticketName);
		});

		return future;
	}

	/**
	 * Creates the webhook for the ticket channel
	 *
	 * @param ticket The ticket to create the webhook for
	 *
	 * @return the completable future
	 */
	public static CompletableFuture<Void> createWebhook(Ticket ticket) {
		CompletableFuture<Void> future = new CompletableFuture<>();

		CompletableFuture.runAsync(() -> {
			TextChannel channel = ticket.getChannel();

			if (channel == null) {
				future.complete(null);
				return;
			}

			channel.createWebhook("Ticket Webhook").queue(webhook -> {
				ticket.setWebhookId(webhook.getId());
				ticket.setWebhookName(webhook.getName());
				ticket.setWebhookUrl(webhook.getUrl());

				future.complete(null);
			}, future::completeExceptionally);
		});

		return future;
	}

	/**
	 * Deletes the ticket channel
	 *
	 * @param ticket The ticket to delete the channel for
	 *
	 * @return The future result
	 */
	public static CompletableFuture<Void> deleteTicketChannel(Ticket ticket) {
		CompletableFuture<Void> future = new CompletableFuture<>();

		TextChannel channel = ticket.getChannel();

		if (channel == null) {
			future.complete(null);
			return future;
		}

		channel.delete().queue(future::complete, future::completeExceptionally);

		return future;
	}

	/**
	 * Check if the ticket exists
	 *
	 * @param newTicketName   The name of the ticket
	 * @param channelCategory The category the ticket should be created in
	 * @param newTicketType   the new ticket type
	 * @param newAuthor       the new author
	 *
	 * @return If the ticket exists
	 */
	public static CompletableFuture<Boolean> checkTicketExists(
			String newTicketName, Category channelCategory,
			TicketType newTicketType, User newAuthor
	) {
		CompletableFuture<Boolean> future = new CompletableFuture<>();

		boolean channelExists = channelCategory.getChannels().stream()
											   .anyMatch(categoryChannel -> categoryChannel.getName().equalsIgnoreCase(
													   newTicketName));

		if (channelExists) {
			future.complete(true);
			return future;
		}

		boolean hasTicket = false;
		for (Ticket ticket : DiscordBot.getInstance().getTicketManager().getTickets()) {
			if (newAuthor.getId().equalsIgnoreCase(ticket.getTicketAuthorId())
				&& newTicketType == ticket.getTicketType()) {
				hasTicket = true;
				break;
			}
		}

		future.complete(hasTicket);

		return future;
	}
}
