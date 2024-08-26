package dev.slne.discord.ticket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.config.discord.GuildConfig;
import dev.slne.discord.config.role.RoleConfig;
import dev.slne.discord.discord.interaction.command.commands.ticket.TicketCloseCommand;
import dev.slne.discord.message.Messages;
import dev.slne.discord.ticket.member.TicketMember;
import dev.slne.discord.ticket.message.TicketMessage;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import org.jetbrains.annotations.Blocking;

/**
 * The type Ticket.
 */
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket {

  private final List<TicketMember> removedMembers = new ArrayList<>();

  @JsonProperty("id")
  @Setter
  private long id = -1;

  @JsonProperty("ticket_id")
  @Setter
  private UUID ticketId;

  @JsonProperty("opened_at")
  @Setter
  private ZonedDateTime openedAt;

  @JsonProperty("guild_id")
  private String guildId;

  @JsonProperty("channel_id")
  @Setter
  private String channelId;

  @JsonProperty("type")
  private String ticketTypeString;

  @JsonProperty("author_id")
  private String ticketAuthorId;

  @JsonProperty("author_name")
  private String ticketAuthorName;

  @JsonProperty("author_avatar_url")
  private String ticketAuthorAvatarUrl;

  @JsonProperty("closed_by_id")
  @Setter
  private String closedById;

  @JsonProperty("closed_reason")
  @Setter
  private String closedReason;

  @JsonProperty("closed_by_avatar_url")
  @Setter
  private String closedByAvatarUrl;

  @JsonProperty("closed_by_name")
  @Setter
  private String closedByName;

  @JsonProperty("closed_at")
  @Setter
  private ZonedDateTime closedAt;

  @JsonProperty("messages")
  private List<TicketMessage> messages = new ArrayList<>();

  @JsonProperty("members")
  private List<TicketMember> members = new ArrayList<>();

  @JsonProperty("created_at")
  @Setter
  private ZonedDateTime createdAt;

  /**
   * Constructor for a ticket
   *
   * @param guild        The guild the ticket is created in
   * @param ticketAuthor The author of the ticket
   * @param ticketType   The type of the ticket
   */
  public Ticket(Guild guild, User ticketAuthor, TicketType ticketType) {
    this.openedAt = ZonedDateTime.now();

    if (guild != null) {
      this.guildId = guild.getId();
    }

    if (ticketType != null) {
      this.ticketTypeString = ticketType.name();
    }

    if (ticketAuthor != null) {
      this.ticketAuthorName = ticketAuthor.getName();
      this.ticketAuthorId = ticketAuthor.getId();
      this.ticketAuthorAvatarUrl = ticketAuthor.getAvatarUrl();
    }
  }

  /**
   * Adds a raw ticket member to the ticket
   *
   * @param ticketMember The ticket member
   */
  public void addRawTicketMember(TicketMember ticketMember) {
    members.add(ticketMember);
  }

  /**
   * Removes a raw ticket member from the ticket
   *
   * @param ticketMember The ticket member
   */
  public void removeRawTicketMember(TicketMember ticketMember) {
    members.remove(ticketMember);
    removedMembers.add(ticketMember);
  }

  /**
   * Adds a raw ticket message to the ticket
   *
   * @param ticketMessage The ticket message
   */
  public void addRawTicketMessage(TicketMessage ticketMessage) {
    messages.add(ticketMessage);
  }

  /**
   * Check if the member exists
   *
   * @param user The user to check
   * @return If the member exists
   */
  @JsonIgnore
  public boolean memberExists(User user) {
    return members.stream()
        .anyMatch(member -> member.getMemberId().equals(user.getId()) && !member.isRemoved());
  }


  /**
   * Adds a role to the ticket channel
   *
   * @param role The role
   * @return The permission override
   */
  public CompletableFuture<PermissionOverride> addTicketRole(Role role) { // TODO: 25.08.2024 15:24 - needed?
    Optional<TextChannel> channel = getChannel();

    if (channel.isEmpty()) {
      return CompletableFuture.completedFuture(null);
    }

    GuildConfig guildConfig = GuildConfig.getConfig(getGuildId());

    if (guildConfig == null) {
      return CompletableFuture.completedFuture(null);
    }

    RoleConfig roleConfig = RoleConfig.getConfig(getGuildId(), role.getName());

    PermissionOverrideAction permissionOverrideAction = channel.get().upsertPermissionOverride(role);
    permissionOverrideAction = permissionOverrideAction.resetAllow();
    permissionOverrideAction = permissionOverrideAction.resetDeny();
    permissionOverrideAction = permissionOverrideAction.setAllowed(
        roleConfig.getDiscordDeniedPermissionsAsJDA());

    return permissionOverrideAction.submit();
  }

  /**
   * Returns the ticket message by the message
   *
   * @param message The message
   * @return The ticket message
   */
  @SuppressWarnings("unused")
  @JsonIgnore
  public Optional<TicketMessage> getTicketMessage(Message message) {
    return messages.stream()
        .filter(ticketMessage -> ticketMessage.getMessageId().equals(message.getId()))
        .findFirst();
  }

  /**
   * Returns the ticket message by the message id
   *
   * @param messageId The message id
   * @return The ticket message
   */
  @JsonIgnore
  public Optional<TicketMessage> getTicketMessage(String messageId) {
    return messages.stream().filter(ticketMessage -> ticketMessage.getMessageId().equals(messageId))
        .findFirst();
  }

  /**
   * Returns the ticket member by the member
   *
   * @param user The member
   * @return The ticket member
   */
  @SuppressWarnings("unused")
  @JsonIgnore
  public Optional<TicketMember> getTicketMember(User user) {
    return members.stream().filter(ticketMember -> ticketMember.getMemberId().equals(user.getId()))
        .findFirst();
  }

  /**
   * Returns the active ticket member by the member
   *
   * @param user The member
   * @return The active ticket member
   */
  @JsonIgnore
  public Optional<TicketMember> getActiveTicketMember(User user) {
    return members.stream()
        .filter(ticketMember -> ticketMember.getMemberId().equals(user.getId()) &&
            !ticketMember.isRemoved())
        .findFirst();
  }

  /**
   * Returns the ticket member by the member id
   *
   * @param userId The member id
   * @return The ticket member
   */
  @SuppressWarnings("unused")
  @JsonIgnore
  public Optional<TicketMember> getTicketMember(String userId) {
    return members.stream()
        .filter(ticketMember -> ticketMember.getMemberId().equals(userId))
        .findFirst();
  }

  /**
   * Gets ticket author.
   *
   * @return the ticketAuthor
   */
  @JsonIgnore
  public Optional<RestAction<User>> getTicketAuthor() {
    if (ticketAuthorId == null) {
      return Optional.empty();
    }

    return Optional.of(DiscordBot.getInstance().getJda().retrieveUserById(ticketAuthorId));
  }

  @JsonIgnore
  @Blocking
  public Optional<User> getTicketAuthorNow() {
    return getTicketAuthor().map(RestAction::complete);
  }

  /**
   * Get the type of the ticket
   *
   * @return The type of the ticket
   */
  @JsonIgnore
  public TicketType getTicketType() {
    return TicketType.valueOf(ticketTypeString);
  }

  /**
   * Get the guild the ticket is created in
   *
   * @return The guild the ticket is created in
   */
  @JsonIgnore
  public Optional<Guild> getGuild() {
    if (guildId == null) {
      return Optional.empty();
    }

    return Optional.ofNullable(DiscordBot.getInstance().getJda().getGuildById(guildId));
  }

  /**
   * Get the channel the ticket is created in
   *
   * @return The channel the ticket is created in
   */
  @JsonIgnore
  public Optional<TextChannel> getChannel() {
    if (channelId == null) {
      return Optional.empty();
    }

    return Optional.ofNullable(DiscordBot.getInstance().getJda().getTextChannelById(channelId));
  }

  /**
   * Gets closed by.
   *
   * @return the closedBy
   */
  @JsonIgnore
  public Optional<RestAction<User>> getClosedBy() {
    if (closedById == null) {
      return Optional.empty();
    }

    return Optional.of(DiscordBot.getInstance().getJda().retrieveUserById(closedById));
  }

  @JsonIgnore
  public Optional<User> getClosedByNow() {
    return getClosedBy().map(RestAction::complete);
  }

  public void updateFrom(Ticket other) {
    setOpenedAt(other.getOpenedAt());
    setTicketId(other.getTicketId());
    setId(other.getId());
    setCreatedAt(other.getCreatedAt());
    setClosedAt(other.getClosedAt());
  }

  @JsonIgnore
  public boolean isPersisted() {
    return id != -1;
  }

  @JsonIgnore
  public boolean hasTicketId() {
    return ticketId != null;
  }

  @JsonIgnore
  public boolean hasGuild() {
    return guildId != null;
  }

  @JsonIgnore
  public String getCloseReasonOrDefault() {
    return closedReason == null ? Messages.DEFAULT_TICKET_CLOSED_REASON : closedReason;
  }
}
