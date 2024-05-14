package dev.slne.discord.ticket.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.ticket.Ticket;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The type Ticket member.
 */
@Setter
@ToString
@EqualsAndHashCode
@Getter
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

	/**
	 * Instantiates a new Ticket member.
	 */
	public TicketMember() {
	}

	/**
	 * Instantiates a new Ticket member.
	 *
	 * @param ticketId  the ticket id
	 * @param memberId  the member id
	 * @param addedById the added by id
	 */
	public TicketMember(UUID ticketId, String memberId, String addedById) {
		this.ticketId = ticketId;
		this.memberId = memberId;
		this.addedById = addedById;
	}

	/**
	 * Constructor for a ticket member
	 *
	 * @param ticket  the ticket
	 * @param member  The member of the ticket
	 * @param addedBy the added by
	 */
	public TicketMember(Ticket ticket, User member, User addedBy) {
		if (member != null) {
			this.memberId = member.getId();
			this.memberName = member.getName();
			this.memberAvatarUrl = member.getAvatarUrl();
		}

		if (addedBy != null) {
			this.addedById = addedBy.getId();
			this.addedByName = addedBy.getName();
			this.addedByAvatarUrl = addedBy.getAvatarUrl();
		}

		this.removedById = null;
		this.removedByName = null;
		this.removedByAvatarUrl = null;

		if (ticket != null) {
			this.ticketId = ticket.getTicketId();
		}
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
	 * Creates the ticket member
	 *
	 * @return The result of the creation
	 */
	public CompletableFuture<TicketMember> create() {
		CompletableFuture<TicketMember> future = new CompletableFuture<>();

		Ticket ticket = getTicket();

		if (ticket == null) {
			future.completeExceptionally(new IllegalStateException("Ticket is null"));
			return future;
		}

		UUID ticketId = ticket.getTicketId();

		if (ticketId == null) {
			future.completeExceptionally(new IllegalStateException("Ticket id is null"));
			return future;
		}

		TicketMemberService.INSTANCE.createTicketMember(ticket, this).thenAccept(future::complete)
									.exceptionally(exception -> {
										future.completeExceptionally(exception);
										return null;
									});

		return future;
	}

	/**
	 * Returns if the ticket member is removed
	 *
	 * @return If the ticket member is removed
	 */
	public boolean isRemoved() {
		return getRemovedBy() != null || removedById != null || removedByName != null || removedByAvatarUrl != null;
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
	public RestAction<User> getMember() {
		if (memberId == null) {
			return null;
		}

		return DiscordBot.getInstance().getJda().retrieveUserById(memberId);
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
