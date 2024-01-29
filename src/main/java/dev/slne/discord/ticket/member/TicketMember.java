package dev.slne.discord.ticket.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonObject;
import dev.slne.data.api.gson.GsonConverter;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.ticket.Ticket;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * The type Ticket member.
 */
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
	private long ticketRawId;

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
	public TicketMember(long ticketId, String memberId, String addedById) {
		this.ticketRawId = ticketId;
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
			this.ticketRawId = ticket.getId();
		}
	}

	/**
	 * Deletes the ticket member
	 *
	 * @return The result of the deletion
	 */
	public CompletableFuture<TicketMember> delete() {
//		Ticket ticket = getTicket();
//
//		if (ticket == null) {
//			future.complete(null);
//			return future;
//		}
//
//		String ticketId = ticket.getTicketId();
//
//		if (ticketId == null) {
//			future.complete(null);
//			return future;
//		}
//
//		CompletableFuture.runAsync(() -> {
//			String url = String.format(API.TICKET_MEMBER, ticketId, id);
//			WebRequest request = WebRequest.builder().json(true).parameters(toDeleteParameters()).url(url).build();
//			request.executeDelete().thenAccept(response -> {
//				if (response.statusCode() == 200) {
//					future.complete(this);
//				} else {
//					future.complete(null);
//				}
//			}).exceptionally(exception -> {
//				DataApi.getDataInstance().logError(getClass(), "Ticket member could not be deleted", exception);
//				future.completeExceptionally(exception);
//				return null;
//			});
//		});
//
//		return future;

		return null;
	}

	/**
	 * Converts the ticket member to a map of parameters
	 *
	 * @return The map of parameters
	 */
	public Map<String, Object> toParameters() {
		Map<String, Object> parameters = new HashMap<>();

		if (memberId != null) {
			parameters.put("member_id", memberId);
		}

		if (memberName != null) {
			parameters.put("member_name", memberName);
		}

		if (memberAvatarUrl != null) {
			parameters.put("member_avatar_url", memberAvatarUrl);
		}

		if (addedById != null) {
			parameters.put("added_by_id", addedById);
		}

		if (addedByName != null) {
			parameters.put("added_by_name", addedByName);
		}

		if (addedByAvatarUrl != null) {
			parameters.put("added_by_avatar_url", addedByAvatarUrl);
		}

		return parameters;
	}

	/**
	 * Converts the ticket member to a map of delete parameters
	 *
	 * @return The map of parameters
	 */
	public Map<String, Object> toDeleteParameters() {
		Map<String, Object> parameters = new HashMap<>();

		if (removedById != null) {
			parameters.put("removed_by_id", removedById);
		}

		if (removedByName != null) {
			parameters.put("removed_by_name", removedByName);
		}

		if (removedByAvatarUrl != null) {
			parameters.put("removed_by_avatar_url", removedByAvatarUrl);
		}

		return parameters;
	}

	/**
	 * Returns the ticket member from a json object
	 *
	 * @return The ticket member
	 */
	private TicketMember fromJsonObject(JsonObject jsonObject) {
		GsonConverter gson = DiscordBot.getInstance().getGsonConverter();

		return gson.fromJson(jsonObject.toString(), TicketMember.class);
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
			future.complete(null);
			return future;
		}

		String ticketId = ticket.getTicketId();

		if (ticketId == null) {
			future.complete(null);
			return future;
		}

		CompletableFuture.runAsync(() -> {
//			String url = String.format(API.TICKET_MEMBERS, ticketId);
//			WebRequest request = WebRequest.builder().url(url).json(true).parameters(toParameters()).build();
//			request.executePost().thenAccept(response -> {
//				TicketMember tempMember =
//						fromJsonObject(response.bodyObject(DiscordBot.getInstance().getGsonConverter()));
//				id = tempMember.id;
//
//				future.complete(this);
//			}).exceptionally(exception -> {
//				DataApi.getDataInstance().logError(getClass(), "Ticket member could not be created", exception);
//				future.completeExceptionally(exception);
//				return null;
//			}); // FIXME: 28.01.2024 00:06 feign
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
	 * Gets id.
	 *
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets id.
	 *
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
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
	 * Gets member id.
	 *
	 * @return the memberId
	 */
	public String getMemberId() {
		return memberId;
	}

	/**
	 * Sets member id.
	 *
	 * @param memberId the memberId to set
	 */
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	/**
	 * Gets ticket.
	 *
	 * @return the ticket
	 */
	@JsonIgnore
	public Ticket getTicket() {
		if (ticketRawId == 0) {
			return null;
		}

		return DiscordBot.getInstance().getTicketManager().getTicketById(ticketRawId);
	}

	/**
	 * Gets ticket raw id.
	 *
	 * @return the ticketId
	 */
	public long getTicketRawId() {
		return ticketRawId;
	}

	/**
	 * Sets ticket raw id.
	 *
	 * @param ticketId the ticketId to set
	 */
	public void setTicketRawId(long ticketId) {
		this.ticketRawId = ticketId;
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
	 * Gets added by id.
	 *
	 * @return the addedById
	 */
	public String getAddedById() {
		return addedById;
	}

	/**
	 * Sets added by id.
	 *
	 * @param addedById the addedById to set
	 */
	public void setAddedById(String addedById) {
		this.addedById = addedById;
	}

	/**
	 * Gets added by avatar url.
	 *
	 * @return the addedByAvatarUrl
	 */
	public String getAddedByAvatarUrl() {
		return addedByAvatarUrl;
	}

	/**
	 * Sets added by avatar url.
	 *
	 * @param addedByAvatarUrl the addedByAvatarUrl to set
	 */
	public void setAddedByAvatarUrl(String addedByAvatarUrl) {
		this.addedByAvatarUrl = addedByAvatarUrl;
	}

	/**
	 * Gets added by name.
	 *
	 * @return the addedByName
	 */
	public String getAddedByName() {
		return addedByName;
	}

	/**
	 * Sets added by name.
	 *
	 * @param addedByName the addedByName to set
	 */
	public void setAddedByName(String addedByName) {
		this.addedByName = addedByName;
	}

	/**
	 * Gets member avatar url.
	 *
	 * @return the memberAvatarUrl
	 */
	public String getMemberAvatarUrl() {
		return memberAvatarUrl;
	}

	/**
	 * Sets member avatar url.
	 *
	 * @param memberAvatarUrl the memberAvatarUrl to set
	 */
	public void setMemberAvatarUrl(String memberAvatarUrl) {
		this.memberAvatarUrl = memberAvatarUrl;
	}

	/**
	 * Gets member name.
	 *
	 * @return the memberName
	 */
	public String getMemberName() {
		return memberName;
	}

	/**
	 * Sets member name.
	 *
	 * @param memberName the memberName to set
	 */
	public void setMemberName(String memberName) {
		this.memberName = memberName;
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

	/**
	 * Gets removed by avatar url.
	 *
	 * @return the removedByAvatarUrl
	 */
	public String getRemovedByAvatarUrl() {
		return removedByAvatarUrl;
	}

	/**
	 * Sets removed by avatar url.
	 *
	 * @param removedByAvatarUrl the removedByAvatarUrl to set
	 */
	public void setRemovedByAvatarUrl(String removedByAvatarUrl) {
		this.removedByAvatarUrl = removedByAvatarUrl;
	}

	/**
	 * Gets removed by id.
	 *
	 * @return the removedById
	 */
	public String getRemovedById() {
		return removedById;
	}

	/**
	 * Sets removed by id.
	 *
	 * @param removedById the removedById to set
	 */
	public void setRemovedById(String removedById) {
		this.removedById = removedById;
	}

	/**
	 * Gets removed by name.
	 *
	 * @return the removedByName
	 */
	public String getRemovedByName() {
		return removedByName;
	}

	/**
	 * Sets removed by name.
	 *
	 * @param removedByName the removedByName to set
	 */
	public void setRemovedByName(String removedByName) {
		this.removedByName = removedByName;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
