package dev.slne.discord.ticket.message;

import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.slne.data.api.DataApi;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.datasource.Times;
import dev.slne.discord.ticket.Ticket;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * The type Ticket message.
 */
public class TicketMessage {

	@JsonProperty("id")
	private long id;

	@JsonProperty("ticket_id")
	private long ticketRawId;

	@JsonProperty("content")
	private String jsonContent;

	@JsonProperty("message_id")
	private String messageId;

	@JsonProperty("author_id")
	private String authorId;

	@JsonProperty("author_name")
	private String authorName;

	@JsonProperty("author_avatar_url")
	private String authorAvatarUrl;

	@JsonProperty("message_created_at")
	private ZonedDateTime messageCreatedAt;

	@JsonProperty("message_edited_at")
	private ZonedDateTime messageEditedAt;

	@JsonProperty("message_deleted_at")
	private ZonedDateTime messageDeletedAt;

	@JsonProperty("references_message_id")
	private String referencesMessageId;

	@JsonProperty("attachments")
	private List<TicketMessageAttachement> attachments;

	@JsonProperty("bot_message")
	private boolean botMessage;

	/**
	 * Instantiates a new Ticket message.
	 */
	public TicketMessage() {
	}

	/**
	 * Constructor for a ticket message
	 *
	 * @param clone The ticket message to clone
	 */
	public TicketMessage(TicketMessage clone) {
		this.ticketRawId = clone.ticketRawId;
		this.messageId = clone.messageId;
		this.jsonContent = clone.jsonContent;

		this.authorId = clone.authorId;
		this.authorName = clone.authorName;
		this.authorAvatarUrl = clone.authorAvatarUrl;

		this.messageCreatedAt = clone.messageCreatedAt;
		this.messageEditedAt = clone.messageEditedAt;
		this.messageDeletedAt = clone.messageDeletedAt;

		this.referencesMessageId = clone.referencesMessageId;

		this.attachments = new ArrayList<>(clone.attachments);
		this.botMessage = clone.botMessage;
	}

	/**
	 * Constructor for a ticket message
	 *
	 * @param ticket  the ticket
	 * @param message The message to create the ticket message from
	 */
	public TicketMessage(Ticket ticket, Message message) {
		this.ticketRawId = ticket.getId();

		this.messageId = message.getId();
		this.jsonContent = message.getContentDisplay();

		this.authorId = message.getAuthor().getId();
		this.authorName = message.getAuthor().getName();
		this.authorAvatarUrl = message.getAuthor().getAvatarUrl();

		LocalDateTime createdUTC = message.getTimeCreated().toLocalDateTime();
		this.messageCreatedAt = Times.convertFromLocalDateTime(createdUTC);

		OffsetDateTime timeEdited = message.getTimeEdited();
		LocalDateTime editedUTC = timeEdited != null ? timeEdited.toLocalDateTime() : null;
		this.messageEditedAt = editedUTC != null ? Times.convertFromLocalDateTime(editedUTC) : null;

		TextChannel channel = ticket.getChannel();
		MessageReference reference = message.getMessageReference();
		if (reference != null && channel != null && reference.getChannel() != null) {
			reference.resolve().queue(
					referencedMessage -> this.referencesMessageId = referencedMessage.getId(),
					failure -> {
						DataApi.getDataInstance().logError(getClass(), "Failed to resolve message reference", failure);
					}
			);
		}

		this.attachments = new ArrayList<>();
		for (Attachment attachment : message.getAttachments()) {
			String name = attachment.getFileName();
			String url = attachment.getUrl();
			String extension = attachment.getFileExtension();
			int size = attachment.getSize();

			String description = attachment.getDescription();

			TicketMessageAttachement attachement = new TicketMessageAttachement(this, name, url, extension, size,
																				description
			);
			this.attachments.add(attachement);
		}

		this.botMessage = message.getAuthor().isBot();
	}

	/**
	 * Returns a ticket message from a message id
	 *
	 * @param id the id
	 *
	 * @return the ticket message
	 */
	public static TicketMessage getByMessageId(long id) {
		return DiscordBot.getInstance().getTicketManager().getTickets().stream().map(Ticket::getMessages)
						 .filter(Objects::nonNull).flatMap(List::stream).filter(message -> message.getId() == id)
						 .findFirst().orElse(null);
	}

	/**
	 * Delete a ticket message
	 * the message id
	 *
	 * @return the {@link CompletableFuture}
	 */
	public CompletableFuture<TicketMessage> delete() {
		CompletableFuture<TicketMessage> future = new CompletableFuture<>();

		CompletableFuture.runAsync(() -> {
			if (this.messageDeletedAt != null) {
				future.complete(this);
				return;
			}

			TicketMessage newMessage = new TicketMessage(this);
			newMessage.messageDeletedAt = ZonedDateTime.now();

			newMessage.create().thenAcceptAsync(future::complete).exceptionally(throwable -> {
				DataApi.getDataInstance().logError(getClass(), "Ticket message could not be deleted", throwable);
				future.complete(null);
				return null;
			});
		});

		return future;
	}

	/**
	 * Prints the ticket message
	 */
	public void printMessage() {
		String content = getContent().join();

		if (getTicket().getChannel() == null || content == null) {
			return;
		}

		getTicket().getWebhook().queue(webhook -> {
			if (webhook == null) {
				return;
			}

			String avatarUrl = authorAvatarUrl;

			try (JDAWebhookClient client = JDAWebhookClient.from(webhook)) {
				WebhookMessageBuilder builder = new WebhookMessageBuilder();
				builder.setUsername(authorName);
				builder.setAvatarUrl(avatarUrl);
				builder.setContent(content);

				client.send(builder.build());
			}
		});
	}

	/**
	 * Saves the ticket message
	 *
	 * @return True if the ticket message has been saved successfully, false
	 */
	public CompletableFuture<TicketMessage> create() {
		CompletableFuture<TicketMessage> future = new CompletableFuture<>();
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
//			String url = String.format(API.TICKET_MESSAGES, ticketId);
//			WebRequest request = WebRequest.builder().url(url).json(true).parameters(toParameters()).build();
//			request.executePost().thenAccept(response -> {
//				TicketMessage tempMessage =
//						fromJsonObject(response.bodyObject(DiscordBot.getInstance().getGsonConverter()));
//				id = tempMessage.id;
//
//				future.complete(this);
//			}).exceptionally(throwable -> {
//				DataApi.getDataInstance().logError(getClass(), "Ticket message could not be created", throwable);
//				future.completeExceptionally(throwable);
//				return null;
//			}); // FIXME: 28.01.2024 00:06 feign
		});

		return future;
	}

	/**
	 * Updates the ticket message
	 *
	 * @param updatedMessage The updated message
	 *
	 * @return The future result
	 */
	public CompletableFuture<TicketMessage> update(Message updatedMessage) {
		CompletableFuture<TicketMessage> future = new CompletableFuture<>();
		TicketMessage newTicketMessage = new TicketMessage(this);

		newTicketMessage.jsonContent = updatedMessage.getContentDisplay();
		newTicketMessage.messageCreatedAt =
				Times.convertFromLocalDateTime(updatedMessage.getTimeCreated().toLocalDateTime());

		OffsetDateTime timeEdited = updatedMessage.getTimeEdited();
		newTicketMessage.messageEditedAt = updatedMessage.isEdited() && timeEdited != null
				? Times.convertFromLocalDateTime(timeEdited.toLocalDateTime())
				: null;

		newTicketMessage.attachments = new ArrayList<>();

		for (Attachment attachment : updatedMessage.getAttachments()) {
			String name = attachment.getFileName();
			String url = attachment.getUrl();
			String extension = attachment.getFileExtension();
			int size = attachment.getSize();

			String description = attachment.getDescription();

			TicketMessageAttachement attachement = new TicketMessageAttachement(newTicketMessage, name, url, extension,
																				size, description
			);
			newTicketMessage.attachments.add(attachement);
		}

		newTicketMessage.create().thenAcceptAsync(future::complete).exceptionally(throwable -> {
			DataApi.getDataInstance().logError(getClass(), "Ticket message could not be updated", throwable);
			future.completeExceptionally(throwable);
			return null;
		});

		return future;
	}

	/**
	 * Gets message.
	 *
	 * @return the message
	 */
	@JsonIgnore
	public RestAction<Message> getMessage() {
		TextChannel channel = getTicket().getChannel();

		if (channel == null) {
			return null;
		}

		return channel.retrieveMessageById(messageId);
	}

	/**
	 * Gets author.
	 *
	 * @return the author
	 */
	@JsonIgnore
	public RestAction<User> getAuthor() {
		if (authorId == null) {
			return null;
		}

		return DiscordBot.getInstance().getJda().retrieveUserById(authorId);
	}

	/**
	 * Gets references message.
	 *
	 * @return the referencesMessage
	 */
	@JsonIgnore
	public RestAction<Message> getReferencesMessage() {
		if (referencesMessageId == null) {
			return null;
		}

		TextChannel channel = getTicket().getChannel();
		if (channel == null) {
			return null;
		}

		return channel.retrieveMessageById(referencesMessageId);
	}

	/**
	 * Gets ticket.
	 *
	 * @return the ticket
	 */
	public Ticket getTicket() {
		return DiscordBot.getInstance().getTicketManager().getTicketById(ticketRawId);
	}

	/**
	 * Gets content.
	 *
	 * @return the content
	 */
	@JsonIgnore
	public CompletableFuture<String> getContent() {
		CompletableFuture<String> future = new CompletableFuture<>();

		if (jsonContent != null) {
			future.complete(jsonContent);
			return future;
		}

		RestAction<Message> message = getMessage();

		if (message == null) {
			future.complete(null);
			return future;
		}

		message.queue(msg -> {
			String content = msg.getContentDisplay();
			future.complete(content);
		});

		return future;
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
	 * Gets ticket raw id.
	 *
	 * @return the ticket raw id
	 */
	public long getTicketRawId() {
		return ticketRawId;
	}

	/**
	 * Gets json content.
	 *
	 * @return the json content
	 */
	public String getJsonContent() {
		return jsonContent;
	}

	/**
	 * Gets message id.
	 *
	 * @return the message id
	 */
	public String getMessageId() {
		return messageId;
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
	 * Gets author name.
	 *
	 * @return the author name
	 */
	public String getAuthorName() {
		return authorName;
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
	 * Gets message created at.
	 *
	 * @return the message created at
	 */
	public ZonedDateTime getMessageCreatedAt() {
		return messageCreatedAt;
	}

	/**
	 * Gets message edited at.
	 *
	 * @return the message edited at
	 */
	public ZonedDateTime getMessageEditedAt() {
		return messageEditedAt;
	}

	/**
	 * Gets message deleted at.
	 *
	 * @return the message deleted at
	 */
	public ZonedDateTime getMessageDeletedAt() {
		return messageDeletedAt;
	}

	/**
	 * Gets references message id.
	 *
	 * @return the references message id
	 */
	public String getReferencesMessageId() {
		return referencesMessageId;
	}

	/**
	 * Gets attachments.
	 *
	 * @return the attachments
	 */
	public List<TicketMessageAttachement> getAttachments() {
		return attachments;
	}

	/**
	 * Is bot message boolean.
	 *
	 * @return the boolean
	 */
	public boolean isBotMessage() {
		return botMessage;
	}
}
