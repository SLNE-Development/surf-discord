package dev.slne.discord.ticket;

import dev.slne.data.api.DataApi;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.config.discord.GuildConfig;
import dev.slne.discord.config.role.RoleConfig;
import dev.slne.discord.discord.guild.permission.DiscordPermission;
import dev.slne.discord.ticket.TicketPermissionOverride.Type;
import dev.slne.discord.ticket.member.TicketMember;
import dev.slne.discord.ticket.result.TicketCreateResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
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
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

/**
 * The type Ticket channel.
 */
@UtilityClass
public class TicketChannelUtil {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("TicketChannelUtil");

  /**
   * Adds a ticket member to the channel
   *
   * @param ticket       the ticket
   * @param ticketMember the ticket member
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

      guild.retrieveMember(user).submit().thenAcceptAsync(member -> {
        if (member == null) {
          future.completeExceptionally(new RuntimeException("Member not found"));
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
      }).exceptionally(throwable -> {
        future.completeExceptionally(throwable);
        return null;
      });
    }, future::completeExceptionally);

    return future;
  }

  /**
   * Removes a ticket member from the channel
   *
   * @param ticket       the ticket
   * @param ticketMember the ticket member
   * @return when completed
   */
  public static CompletableFuture<Void> removeTicketMember(Ticket ticket,
      TicketMember ticketMember) {
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

      manager.removePermissionOverride(user.getIdLong())
          .queue(future::complete, future::completeExceptionally);
    }, future::completeExceptionally);

    return future;
  }

  /**
   * Returns the default permission overrides for the ticket channel
   *
   * @param ticket The ticket
   * @param author The author of the ticket
   * @return The default permission overrides for the ticket channel
   */
  public static List<TicketPermissionOverride> getChannelPermissions(Ticket ticket, User author) {
    Guild guild = ticket.getGuild();
    TicketType ticketType = ticket.getTicketType();

    List<Permission> allPermissions = getAllPermissions();
    List<TicketPermissionOverride> overrides = new ArrayList<>();

    // Deny public role
    overrides.add(new TicketPermissionOverride(Type.ROLE, guild.getPublicRole().getIdLong(),
        new ArrayList<>(),
        allPermissions
    ));

    // Allow bot user
    overrides.add(
        new TicketPermissionOverride(Type.USER,
            DiscordBot.getInstance().getJda().getSelfUser().getIdLong(),
            allPermissions, new ArrayList<>()
        ));

    // Allow bot role
    Role botRole = guild.getBotRole();
    if (botRole != null) {
      overrides.add(new TicketPermissionOverride(Type.ROLE, botRole.getIdLong(), allPermissions,
          new ArrayList<>()
      ));
    }

    Map<String, RoleConfig> roleConfigMap = GuildConfig.getByGuildId(guild.getId()).getRoleConfig();
    for (Map.Entry<String, RoleConfig> entry : roleConfigMap.entrySet()) {
      RoleConfig roleConfig = entry.getValue();

      if (roleConfig == null) {
        continue;
      }

      if (!roleConfig.canViewTicketType(ticketType)) {
        for (String roleId : roleConfig.getDiscordRoleIds()) {
          Role role = guild.getRoleById(roleId);

          if (role != null) {
            overrides.add(
                new TicketPermissionOverride(Type.ROLE, role.getIdLong(), new ArrayList<>(),
                    allPermissions
                ));
          }
        }
      } else {
        for (String roleId : roleConfig.getDiscordRoleIds()) {
          Role role = guild.getRoleById(roleId);

          if (role != null) {
            overrides.add(new TicketPermissionOverride(Type.ROLE, role.getIdLong(), allPermissions,
                new ArrayList<>()
            ));
          }
        }
      }
    }

    RoleConfig defaultRole = RoleConfig.getDefaultRole(guild.getId());

    // Apply author
    overrides.add(
        new TicketPermissionOverride(
            Type.USER, author.getIdLong(),
            defaultRole.getDiscordAllowedPermissions().stream()
                .map(DiscordPermission::getPermission)
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
   * @return The result of the ticket member creation
   */
  private static CompletableFuture<TicketMember> createAuthorTicketMember(
      Ticket ticket, User author,
      ChannelAction<TextChannel> channelAction
  ) {
    TicketMember ticketMember = new TicketMember(ticket, author,
        DiscordBot.getInstance().getJda().getSelfUser());
    RoleConfig defaultRole = RoleConfig.getDefaultRole(ticket.getGuildId());
    TicketPermissionOverride override = new TicketPermissionOverride(Type.USER, author.getIdLong(),
        defaultRole.getDiscordAllowedPermissions()
            .stream().map(
                DiscordPermission::getPermission)
            .toList(),
        new ArrayList<>()
    );

    //noinspection ResultOfMethodCallIgnored
    channelAction.addMemberPermissionOverride(author.getIdLong(), override.allow(),
        override.deny());

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
   * @return If the permissions list contains the given permission
   */
  private static boolean permissionAdded(Collection<Permission> permissions,
      Permission permission) {
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
            .queue(
                author -> createAuthorTicketMember(ticket, author, channelAction).thenAcceptAsync(
                    v -> {
                      List<TicketPermissionOverride> overrides = getChannelPermissions(ticket,
                          author);
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

                        TicketService.INSTANCE.updateTicket(ticket).thenAcceptAsync(v1 -> {
                          future.complete(TicketCreateResult.SUCCESS);
                        }).exceptionally(throwable -> {
                          future.complete(TicketCreateResult.ERROR);
                          DataApi.getDataInstance()
                              .logError(TicketChannelUtil.class, "Failed to update ticket.",
                                  throwable);
                          return null;
                        });
                      }, exception -> handleException(exception, future));
                    }).exceptionally(throwable -> {
                  future.complete(TicketCreateResult.ERROR);
                  DataApi.getDataInstance()
                      .logError(TicketChannelUtil.class, "Failed to create ticket channel.",
                          throwable);
                  return null;
                }), failure -> {
                  future.complete(TicketCreateResult.ERROR);
                  DataApi.getDataInstance()
                      .logError(TicketChannelUtil.class, "Failed to create ticket channel.",
                          failure);
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
  private static void handleException(Throwable throwable,
      CompletableFuture<TicketCreateResult> future) {
    if (throwable instanceof ErrorResponseException errorResponseException
        && errorResponseException.getErrorCode() == 50013) {
      future.complete(TicketCreateResult.MISSING_PERMISSIONS);
      return;
    } else if (throwable instanceof InsufficientPermissionException) {
      future.complete(TicketCreateResult.MISSING_PERMISSIONS);
      return;
    }

    future.complete(TicketCreateResult.ERROR);
    DataApi.getDataInstance()
        .logError(TicketChannelUtil.class, "Failed to create ticket channel.", throwable);
  }

  /**
   * Get the name for the ticket channel
   *
   * @param ticket The ticket to get the name for
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

      future.complete(generateTicketNameFast(ticketType, ticketAuthor));
    });

    return future;
  }

  public @NotNull String generateTicketNameFast(@NotNull TicketType expectedType,
      @NotNull User expectedAuthor) {
    final String ticketTypeName = expectedType.name().toLowerCase();
    final String authorName = expectedAuthor.getName().toLowerCase().trim().replace(" ", "-");
    final String ticketName = ticketTypeName + "-" + authorName;

    return ticketName.substring(0, Math.min(ticketName.length(), Channel.MAX_NAME_LENGTH));
  }

  /**
   * Deletes the ticket channel
   *
   * @param ticket The ticket to delete the channel for
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
   * @return If the ticket exists
   */
  public static CompletableFuture<Boolean> checkTicketExists(
      String newTicketName, Category channelCategory,
      TicketType newTicketType, User newAuthor
  ) {
    CompletableFuture<Boolean> future = new CompletableFuture<>();

    if (containsChannelName(channelCategory, newTicketName)) {
      future.complete(true);
    } else if (containsChannelName(channelCategory, newTicketName)) {
      future.complete(true);
    } else {
      future.complete(hasAuthorTicketOfType(newTicketType, newAuthor));
    }

    return future;
  }

  public boolean checkTicketExistsFast(
      Guild guild,
      TicketType expectedType,
      User expectedAuthor
  ) {
    final GuildConfig guildConfig = GuildConfig.getByGuildId(guild.getId());

    if (guildConfig == null) {
      LOGGER.error("GuildConfig not found for guild {}. Preventing ticket creation.",
          guild.getId());
      return true;
    }

    final String categoryId = guildConfig.getCategoryId();
    final Category channelCategory = guild.getCategoryById(categoryId);

    if (channelCategory == null) {
      LOGGER.error("Category not found for guild {}. Preventing ticket creation.",
          guild.getId());
      return true;
    }

    return checkTicketExistsFast(channelCategory, expectedType, expectedAuthor);
  }

  public boolean checkTicketExistsFast(
      Category category,
      TicketType expectedType,
      User expectedAuthor
  ) {
    final String expectedTicketName = generateTicketNameFast(expectedType, expectedAuthor);

    if (containsChannelName(category, expectedTicketName)) {
      return true;
    }

    return hasAuthorTicketOfType(expectedType, expectedAuthor);
  }

  public boolean containsChannelName(Category category, String name) {
    return category.getChannels().stream()
        .anyMatch(channel -> channel.getName().equalsIgnoreCase(name));
  }

  public boolean hasAuthorTicketOfType(TicketType type, User user) {
    return DiscordBot.getInstance().getTicketManager().getTickets().stream()
        .filter(ticket -> ticket.getTicketAuthorId().equals(user.getId()))
        .anyMatch(ticket -> ticket.getTicketType().equals(type));
  }
}
