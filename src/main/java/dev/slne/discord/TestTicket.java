package dev.slne.discord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * The type Test ticket.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestTicket {

	@JsonProperty("id")
	private long id;

	@JsonProperty("ticket_id")
	private String ticketId;

	@JsonProperty("ticket_number")
	private String authorId;

	@JsonProperty("author_avatar_url")
	private String authorAvatarUrl;

	@JsonProperty("author_name")
	private String authorName;

	@JsonProperty("guild_id")
	private String guildId;

	@JsonProperty("channel_id")
	private String channelId;

	@JsonProperty("type")
	private String type;

	@JsonProperty("closed_by_id")
	private String closedById;

	@JsonProperty("closed_by_name")
	private String closedByName;

	@JsonProperty("closed_by_avatar_url")
	private String closedByAvatarUrl;

//	@JsonProperty("members")
//	private TestTicketMember[] members;

	/**
	 * Instantiates a new Test ticket.
	 *
	 * @param id                the id
	 * @param ticketId          the ticket id
	 * @param authorId          the author id
	 * @param authorAvatarUrl   the author avatar url
	 * @param authorName        the author name
	 * @param guildId           the guild id
	 * @param channelId         the channel id
	 * @param type              the type
	 * @param closedById        the closed by id
	 * @param closedByName      the closed by name
	 * @param closedByAvatarUrl the closed by avatar url
	 */
	public TestTicket(
			long id, String ticketId, String authorId, String authorAvatarUrl, String authorName, String guildId,
			String channelId, String type, String closedById, String closedByName, String closedByAvatarUrl
	) {
		this.id = id;
		this.ticketId = ticketId;
		this.authorId = authorId;
		this.authorAvatarUrl = authorAvatarUrl;
		this.authorName = authorName;
		this.guildId = guildId;
		this.channelId = channelId;
		this.type = type;
		this.closedById = closedById;
		this.closedByName = closedByName;
		this.closedByAvatarUrl = closedByAvatarUrl;
	}

	/**
	 * Instantiates a new Test ticket.
	 */
	public TestTicket() {
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
	 * Gets ticket id.
	 *
	 * @return the ticket id
	 */
	public String getTicketId() {
		return ticketId;
	}

	/**
	 * Gets author id.
	 *
	 * @return the author id
	 */
	public String getAuthorId() {
		return authorId;
	}

	/**
	 * Gets author avatar url.
	 *
	 * @return the author avatar url
	 */
	public String getAuthorAvatarUrl() {
		return authorAvatarUrl;
	}

	/**
	 * Gets author name.
	 *
	 * @return the author name
	 */
	public String getAuthorName() {
		return authorName;
	}

	/**
	 * Gets guild id.
	 *
	 * @return the guild id
	 */
	public String getGuildId() {
		return guildId;
	}

	/**
	 * Gets channel id.
	 *
	 * @return the channel id
	 */
	public String getChannelId() {
		return channelId;
	}

	/**
	 * Gets type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Gets closed by id.
	 *
	 * @return the closed by id
	 */
	public String getClosedById() {
		return closedById;
	}

	/**
	 * Gets closed by name.
	 *
	 * @return the closed by name
	 */
	public String getClosedByName() {
		return closedByName;
	}

	/**
	 * Gets closed by avatar url.
	 *
	 * @return the closed by avatar url
	 */
	public String getClosedByAvatarUrl() {
		return closedByAvatarUrl;
	}

	/**
	 * Gets members.
	 *
	 * @return the members
	 */
//	public TestTicketMember[] getMembers() {
//		return members;
//	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
