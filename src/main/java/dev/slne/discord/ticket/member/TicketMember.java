package dev.slne.discord.ticket.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.spring.service.ticket.TicketMemberService;
import dev.slne.discord.ticket.Ticket;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.Blocking;

/**
 * The type Ticket member.
 */
@Setter
@ToString
@EqualsAndHashCode
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketMember {

  @JsonProperty("id")
  private long id;

  @JsonProperty("member_id")
  private String memberId;

  @JsonProperty("member_name")
  private String memberName;

  @JsonProperty("member_avatar_url")
  private String memberAvatarUrl;

  @JsonProperty("added_by_id")
  private String addedById;

  @JsonProperty("added_by_name")
  private String addedByName;

  @JsonProperty("added_by_avatar_url")
  private String addedByAvatarUrl;

  @JsonProperty("removed_by_id")
  private String removedById;

  @JsonProperty("removed_by_name")
  private String removedByName;

  @JsonProperty("removed_by_avatar_url")
  private String removedByAvatarUrl;

  @JsonProperty("ticket_id")
  private UUID ticketId;

  public static TicketMember createFromTicket(
      Ticket ticket,
      User member,
      User addedBy
  ) {
    final TicketMemberBuilder builder = TicketMember.builder();

    if (member != null) {
      builder.memberId(member.getId())
          .memberName(member.getName())
          .memberAvatarUrl(member.getAvatarUrl());
    }

    if (addedBy != null) {
      builder.addedById(addedBy.getId())
          .addedByName(addedBy.getName())
          .addedByAvatarUrl(addedBy.getAvatarUrl());
    }

    if (ticket != null) {
      builder.ticketId(ticket.getTicketId());
    }

    return builder.build();
  }

  /**
   * Deletes the ticket member
   *
   * @return The result of the deletion
   */
  public CompletableFuture<TicketMember> delete() {
    return TicketMemberService.INSTANCE.updateTicketMember(getTicket(), this);
  }

  /**
   * Returns if the ticket member is removed
   *
   * @return If the ticket member is removed
   */
  @JsonIgnore
  public boolean isRemoved() {
    return getRemovedBy() != null || removedById != null || removedByName != null
           || removedByAvatarUrl != null;
  }

  /**
   * Returns if the ticket member is activated
   *
   * @return If the ticket member is activated
   */
  @JsonIgnore
  public boolean isActivated() {
    return !isRemoved();
  }

  /**
   * Gets ticket.
   *
   * @return the ticket
   */
  @JsonIgnore
  public Ticket getTicket() {
    return DiscordBot.getInstance().getTicketManager().getTicketById(ticketId);
  }

  /**
   * Gets member.
   *
   * @return the member
   */
  @JsonIgnore
  public Optional<RestAction<User>> getMember() {
    if (memberId == null) {
      return Optional.empty();
    }

    return Optional.of(DiscordBot.getInstance().getJda().retrieveUserById(memberId));
  }

  @JsonIgnore
  @Blocking
  public Optional<User> getMemberNow() {
    return getMember().map(RestAction::complete);
  }

  /**
   * Gets added by.
   *
   * @return the addedBy
   */
  @JsonIgnore
  public RestAction<User> getAddedBy() {
    if (addedById == null) {
      return null;
    }

    return DiscordBot.getInstance().getJda().retrieveUserById(addedById);
  }

  /**
   * Gets removed by.
   *
   * @return the removedBy
   */
  @JsonIgnore
  public RestAction<User> getRemovedBy() {
    if (removedById == null) {
      return null;
    }

    return DiscordBot.getInstance().getJda().retrieveUserById(removedById);
  }
}
