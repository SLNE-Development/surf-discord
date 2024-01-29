package dev.slne.discord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.ZonedDateTime;

/**
 * The type Test ticket member.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestTicketMember {

	@JsonProperty("ticket_id")
	private long ticketId;

	@JsonProperty("member_id")
	private String memberId;

	@JsonProperty("added_by_id")
	private String addedById;

	@JsonProperty("id")
	private long id;

	@JsonProperty("member_name")
	private String memberName;

	@JsonProperty("member_avatar_url")
	private String memberAvatarUrl;

	@JsonProperty("added_at")
	private ZonedDateTime addedAt;

	@JsonProperty("added_by_name")
	private String addedByAvatarUrl;

	@JsonProperty("removed_at")
	private ZonedDateTime removedAt;

	@JsonProperty("removed_by_id")
	private String removedById;

	@JsonProperty("removed_by_name")
	private String removedByName;

	@JsonProperty("removed_by_avatar_url")
	private String removedByAvatarUrl;

	/**
	 * Instantiates a new Test ticket member.
	 */
	public TestTicketMember() {
	}

	/**
	 * Instantiates a new Test ticket member.
	 *
	 * @param ticket_id   the ticket id
	 * @param member_id   the member id
	 * @param added_by_id the added by id
	 */
	public TestTicketMember(long ticket_id, String member_id, String added_by_id) {
		this.ticketId = ticket_id;
		this.memberId = member_id;
		this.addedById = added_by_id;
	}

	/**
	 * Gets ticket id.
	 *
	 * @return the ticket id
	 */
	public long getTicketId() {
		return ticketId;
	}

	/**
	 * Gets member id.
	 *
	 * @return the member id
	 */
	public String getMemberId() {
		return memberId;
	}

	/**
	 * Gets added by id.
	 *
	 * @return the added by id
	 */
	public String getAddedById() {
		return addedById;
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
	 * Gets member name.
	 *
	 * @return the member name
	 */
	public String getMemberName() {
		return memberName;
	}

	/**
	 * Gets member avatar url.
	 *
	 * @return the member avatar url
	 */
	public String getMemberAvatarUrl() {
		return memberAvatarUrl;
	}

	/**
	 * Gets added at.
	 *
	 * @return the added at
	 */
	public ZonedDateTime getAddedAt() {
		return addedAt;
	}

	/**
	 * Gets added by avatar url.
	 *
	 * @return the added by avatar url
	 */
	public String getAddedByAvatarUrl() {
		return addedByAvatarUrl;
	}

	/**
	 * Gets removed at.
	 *
	 * @return the removed at
	 */
	public ZonedDateTime getRemovedAt() {
		return removedAt;
	}

	/**
	 * Gets removed by id.
	 *
	 * @return the removed by id
	 */
	public String getRemovedById() {
		return removedById;
	}

	/**
	 * Gets removed by name.
	 *
	 * @return the removed by name
	 */
	public String getRemovedByName() {
		return removedByName;
	}

	/**
	 * Gets removed by avatar url.
	 *
	 * @return the removed by avatar url
	 */
	public String getRemovedByAvatarUrl() {
		return removedByAvatarUrl;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
