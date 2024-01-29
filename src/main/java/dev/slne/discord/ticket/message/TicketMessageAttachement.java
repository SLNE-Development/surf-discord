package dev.slne.discord.ticket.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The type Ticket message attachement.
 */
public class TicketMessageAttachement {

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
	 * Instantiates a new Ticket message attachement.
	 */
	public TicketMessageAttachement() {
	}

	/**
	 * Constructor for a ticket message attachement
	 *
	 * @param clone The ticket message attachement to clone
	 */
	public TicketMessageAttachement(TicketMessageAttachement clone) {
		this.name = clone.name;
		this.url = clone.url;
		this.extension = clone.extension;
		this.size = clone.size;
		this.description = clone.description;

		this.messageId = clone.messageId;
	}

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
	public TicketMessageAttachement(
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

	/**
	 * Get the name of the attachement
	 *
	 * @return The name of the attachement
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the url of the attachement
	 *
	 * @return The url of the attachement
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Get the extension of the attachement
	 *
	 * @return The extension of the attachement
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * Get the size of the attachement
	 *
	 * @return The size of the attachement
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Get the description of the attachement
	 *
	 * @return The description of the attachement
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets id.
	 *
	 * @return the id
	 */
	public long getId() {
		return id;
	}

}
