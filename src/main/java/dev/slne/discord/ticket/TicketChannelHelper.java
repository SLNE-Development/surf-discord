package dev.slne.discord.ticket;

import dev.slne.discord.config.discord.GuildConfig;
import dev.slne.discord.config.role.RoleConfig;
import dev.slne.discord.exception.ticket.DeleteTicketChannelException;
import dev.slne.discord.exception.ticket.UnableToGetTicketNameException;
import dev.slne.discord.exception.ticket.member.TicketAddMemberException;
import dev.slne.discord.exception.ticket.member.TicketRemoveMemberException;
import dev.slne.discord.guild.permission.DiscordPermission;
import dev.slne.discord.spring.service.ticket.TicketService;
import dev.slne.discord.ticket.TicketPermissionOverride.Type;
import dev.slne.discord.ticket.member.TicketMember;
import dev.slne.discord.ticket.result.TicketCreateResult;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import net.dv8tion.jda.api.JDA;
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
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * The type Ticket channel.
 */
@Component
@ParametersAreNonnullByDefault
public class TicketChannelHelper {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("TicketChannelHelper");
  private final JDA jda;
  private final TicketService ticketService;

  @Autowired
  public TicketChannelHelper(JDA jda, TicketService ticketService) {
    this.jda = jda;
    this.ticketService = ticketService;
  }

  /**
   * Adds a ticket member to the channel
   *
   * @param ticket       the ticket
   * @param ticketMember the ticket member
   * @return when completed
   */
  @Async
  public CompletableFuture<Void> addTicketMember(Ticket ticket, TicketMember ticketMember)
      throws TicketAddMemberException {
    final TicketMember addedMember = ticketService.addTicketMember(ticket, ticketMember).join();
    final Guild guild = ticket.getGuild();
    final TextChannel channel = ticket.getChannel();
    final RestAction<User> userRest = addedMember.getMember();


    if (guild == null || channel == null || userRest == null) {
      throw new TicketAddMemberException("Guild, channel or user not found");
    }

    final TextChannelManager manager = channel.getManager();
    final User user = userRest.complete();

    if (user == null) {
      throw new TicketAddMemberException("User not found");
    }

    final Member member = guild.retrieveMember(user).submit().join();

    if (member == null) {
      throw new TicketAddMemberException("Member not found");
    }

    final List<Role> memberDiscordRoles = member.getRoles();
    final List<RoleConfig> memberRoles = memberDiscordRoles.stream()
        .map(role -> RoleConfig.getDiscordRoleRoles(guild.getId(), role.getId()))
        .flatMap(List::stream)
        .toList();

    final Set<Permission> permissions = memberRoles.stream()
        .map(RoleConfig::getDiscordAllowedPermissions)
        .flatMap(List::stream)
        .map(DiscordPermission::getPermission)
        .collect(Collectors.toSet());

    return CompletableFuture.completedFuture(
        manager.putMemberPermissionOverride(user.getIdLong(), permissions, null)
            .complete()
    );
  }

  /**
   * Removes a ticket member from the channel
   *
   * @param ticket       the ticket
   * @param ticketMember the ticket member
   * @return when completed
   */
  @Async
  public CompletableFuture<Void> removeTicketMember(
      Ticket ticket,
      TicketMember member,
      @NotNull User remover
  ) throws TicketRemoveMemberException {
    final TextChannel channel = ticket.getChannel()
        .orElseThrow(() -> );
    final User user = member.getMemberNow()
        .orElseThrow(() -> );

    ticketService.removeTicketMember(ticket, member, remover).join();

    final TextChannelManager manager = channel.getManager();

    return CompletableFuture.completedFuture(
        manager.removePermissionOverride(user.getIdLong()).complete()
    );
  }

  /**
   * Returns the default permission overrides for the ticket channel
   *
   * @param ticket The ticket
   * @param author The author of the ticket
   * @return The default permission overrides for the ticket channel
   */
  public ObjectList<TicketPermissionOverride> getChannelPermissions(Ticket ticket, User author) {
    final Guild guild = ticket.getGuild();
    final TicketType ticketType = ticket.getTicketType();

    final ObjectSet<Permission> allPermissions = getAllPermissions();
    final ObjectList<TicketPermissionOverride> overrides = new ObjectArrayList<>();

    // Deny public role
    overrides.add(TicketPermissionOverride.builder()
        .type(Type.ROLE)
        .id(guild.getPublicRole().getIdLong())
        .allow(null)
        .deny(allPermissions)
        .build());

    // Allow bot user
    overrides.add(TicketPermissionOverride.builder()
        .type(Type.USER)
        .id(jda.getSelfUser().getIdLong())
        .allow(allPermissions)
        .deny(null)
        .build());

    // Allow bot role
    final Role botRole = guild.getBotRole();
    if (botRole != null) {
      overrides.add(TicketPermissionOverride.builder()
          .type(Type.ROLE)
          .id(botRole.getIdLong())
          .allow(allPermissions)
          .deny(null)
          .build());
    }

    final Object2ObjectMap<String, RoleConfig> roleConfigMap = GuildConfig.getByGuild(guild)
        .getRoleConfig();

    for (final RoleConfig roleConfig : roleConfigMap.values()) {
      if (roleConfig == null) {
        continue;
      }

      final @Nullable ObjectSet<Permission> allowedPermissions;
      final @Nullable ObjectSet<Permission> deniedPermissions;

      if (roleConfig.canViewTicketType(ticketType)) {
        allowedPermissions = allPermissions;
        deniedPermissions = null;
      } else {
        allowedPermissions = null;
        deniedPermissions = allPermissions;
      }

      for (final String roleId : roleConfig.getDiscordRoleIds()) {
        final Role role = guild.getRoleById(roleId);

        if (role != null) {
          overrides.add(TicketPermissionOverride.builder()
              .type(Type.ROLE)
              .id(role.getIdLong())
              .allow(allowedPermissions)
              .deny(deniedPermissions)
              .build());
        }
      }
    }

    final RoleConfig defaultRole = RoleConfig.getDefaultRole(guild.getId());

    // Apply author
    overrides.add(TicketPermissionOverride.builder()
        .type(Type.USER)
        .id(author.getIdLong())
        .deny(null)
        .allow(defaultRole.getDiscordAllowedPermissions().stream()
            .map(DiscordPermission::getPermission)
            .toList())
        .build());

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
  @Async
  protected CompletableFuture<TicketMember> createAuthorTicketMember(
      Ticket ticket,
      User author,
      ChannelAction<TextChannel> channelAction
  ) {
    final TicketMember member = new TicketMember(ticket, author, jda.getSelfUser());
    final RoleConfig defaultRole = RoleConfig.getDefaultRole(ticket.getGuildId());
    final List<Permission> allowedPermissions = defaultRole.getDiscordAllowedPermissions()
        .stream()
        .map(DiscordPermission::getPermission)
        .toList();

    //noinspection ResultOfMethodCallIgnored
    channelAction.addMemberPermissionOverride(
        author.getIdLong(),
        allowedPermissions,
        null
    );

    return ticket.addTicketMember(member);
  }

  /**
   * Returns all permissions
   *
   * @return all permissions
   */
  private static @NotNull ObjectSet<Permission> getAllPermissions() {
    final ObjectSet<Permission> allPermissions = new ObjectArraySet<>();

    for (final Permission perm : Permission.values()) {
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
   * Create the ticket channel
   *
   * @param ticket     The ticket to create the channel for
   * @param ticketName The name of the ticket
   * @param category   The category to create the ticket in
   * @return The result of the ticket creation
   */
  @Async
  public CompletableFuture<TicketCreateResult> createTicketChannel(
      Ticket ticket,
      String ticketName,
      Category category
  ) {
    if (!ticket.hasGuild()) {
      return CompletableFuture.completedFuture(TicketCreateResult.GUILD_NOT_FOUND);
    }

    try {
      final ChannelAction<TextChannel> channelAction = category.createTextChannel(ticketName);
      final User author = ticket.getTicketAuthorNow();
      createAuthorTicketMember(ticket, author, channelAction).join();

      getChannelPermissions(ticket, author)
          .forEach(override -> override.addOverride(channelAction));

      final TextChannel ticketChannel = channelAction.complete();
      ticket.setChannelId(ticketChannel.getId());
      ticketService.updateTicket(ticket).join();

      // Ablauf:
      //1. Channel erstellen
      // 2. Member adden
      // 3. Nachrichten senden (openening)
//      ticketChannel.createThreadChannel("Test", true)
//          .flatMap(threadChannel -> {
//            List<RestAction<Void>> actions = new ObjectArrayList<>();
//            actions.add(threadChannel.addThreadMember(author));
//
//            return RestAction.allOf(actions);
//          });

      return CompletableFuture.completedFuture(TicketCreateResult.SUCCESS);
    } catch (Exception exception) {
      return CompletableFuture.completedFuture(handleCreationException(exception));
    }
  }

  private static TicketCreateResult handleCreationException(Throwable throwable) {
    if (throwable instanceof ErrorResponseException errorResponseException
        && errorResponseException.getErrorCode() == 50013) {
      return TicketCreateResult.MISSING_PERMISSIONS;
    } else if (throwable instanceof InsufficientPermissionException) {
      return TicketCreateResult.MISSING_PERMISSIONS;
    } else {
      LOGGER.error("Failed to create ticket channel.", throwable);
      return TicketCreateResult.ERROR;
    }
  }

  /**
   * Get the name for the ticket channel
   *
   * @param ticket The ticket to get the name for
   * @return The name for the ticket channel
   */
  @Async
  public CompletableFuture<String> getTicketName(Ticket ticket)
      throws UnableToGetTicketNameException {
    final TicketType ticketType = ticket.getTicketType();
    final User author = ticket.getTicketAuthorNow();

    if (ticketType == null || author == null) {
      throw new UnableToGetTicketNameException("Ticket type or author not found");
    }

    return CompletableFuture.completedFuture(generateTicketName(ticketType, author));
  }

  public @NotNull String generateTicketName(
      @NotNull TicketType expectedType,
      @NotNull User expectedAuthor
  ) {
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
  @Async
  public CompletableFuture<Void> deleteTicketChannel(Ticket ticket)
      throws DeleteTicketChannelException {
    final TextChannel channel = ticket.getChannel();

    if (channel == null) {
      throw new DeleteTicketChannelException("Channel not found");
    }

    try {
      channel.delete().complete();
      ticketService.removeTicket(ticket);
    } catch (Exception exception) {
      throw new DeleteTicketChannelException("Failed to delete ticket channel", exception);
    }

    return CompletableFuture.completedFuture(null);
  }

  public boolean checkTicketExists(
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

    return checkTicketExists(channelCategory, expectedType, expectedAuthor);
  }

  public boolean checkTicketExists(
      Category category,
      TicketType expectedType,
      User expectedAuthor
  ) {
    return checkTicketExists(
        generateTicketName(expectedType, expectedAuthor),
        category,
        expectedType,
        expectedAuthor
    );
  }

  public boolean checkTicketExists(
      String ticketChannelName,
      Category category,
      TicketType expectedType,
      User expectedAuthor
  ) {
    if (containsChannelName(category, ticketChannelName)) {
      return true;
    }

    return hasAuthorTicketOfType(expectedType, expectedAuthor);
  }

  public boolean containsChannelName(Category category, String name) {
    return category.getChannels().stream()
        .anyMatch(channel -> channel.getName().equalsIgnoreCase(name));
  }

  public boolean hasAuthorTicketOfType(TicketType type, User user) {
    return ticketService.getTickets().stream()
        .filter(ticket -> ticket.getTicketAuthorId().equals(user.getId()))
        .anyMatch(ticket -> ticket.getTicketType().equals(type));
  }
}
