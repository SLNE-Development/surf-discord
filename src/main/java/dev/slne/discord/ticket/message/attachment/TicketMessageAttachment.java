package dev.slne.discord.ticket.message.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.slne.discord.ticket.message.TicketMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * The type Ticket message attachment.
 */
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Builder
public class TicketMessageAttachment {

	@JsonProperty("id")
	private long id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("url")
	private String url;

	@JsonProperty("extension")
	private String extension;

	@JsonProperty("size")
	private int size;

	@JsonProperty("description")
	private String description;

	@JsonProperty("message_id")
	private long messageId;

	/**
	 * Constructor for a ticket message attachement
	 *
	 * @param message     The message the attachement is attached to
	 * @param name        The name of the attachement
	 * @param url         The url of the attachement
	 * @param extension   The extension of the attachement
	 * @param size        The size of the attachement
	 * @param description The description of the attachement
	 */
	public TicketMessageAttachment(
			TicketMessage message, String name, String url, String extension, int size,
			String description
	) {
		this.name = name;
		this.url = url;
		this.extension = extension;
		this.size = size;
		this.description = description;

		this.messageId = message.getId();
	}

	public static TicketMessageAttachment copy(TicketMessageAttachment clone) {
		TicketMessageAttachment attachment = new TicketMessageAttachment();

		attachment.name = clone.name;
		attachment.url = clone.url;
		attachment.extension = clone.extension;
		attachment.size = clone.size;
		attachment.description = clone.description;

		attachment.messageId = clone.messageId;

		return attachment;
	}

	/**
	 * Get the message the attachement is attached to
	 *
	 * @return The message the attachement is attached to
	 */
	@JsonIgnore
	public TicketMessage getMessage() {
		if (messageId == 0) {
			return null;
		}

		return TicketMessage.getByMessageId(messageId);
	}

}
