package dev.slne.discord.ticket.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.slne.data.api.DataApi;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.datasource.Times;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.message.attachment.TicketMessageAttachment;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The type Ticket message.
 */
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class TicketMessage {

	@JsonProperty("id")
	private long id;

	@JsonProperty("ticket_id")
	private UUID ticketId;

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
	private List<TicketMessageAttachment> attachments;

	@JsonProperty("bot_message")
	private boolean botMessage;

	/**
	 * Constructor for a ticket message
	 *
	 * @param ticket  the ticket
	 * @param message The message to create the ticket message from
	 */
	public TicketMessage(Ticket ticket, Message message) {
		this.ticketId = ticket.getTicketId();

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

			TicketMessageAttachment attachement = new TicketMessageAttachment(this, name, url, extension, size,
																			  description
			);
			this.attachments.add(attachement);
		}

		this.botMessage = message.getAuthor().isBot();
	}

	/**
	 * Copy ticket message.
	 *
	 * @param clone the clone
	 *
	 * @return the ticket message
	 */
	public static TicketMessage copy(TicketMessage clone) {
		TicketMessage ticketMessage = new TicketMessage();

		ticketMessage.ticketId = clone.ticketId;
		ticketMessage.messageId = clone.messageId;
		ticketMessage.jsonContent = clone.jsonContent;
		ticketMessage.authorId = clone.authorId;
		ticketMessage.authorName = clone.authorName;
		ticketMessage.authorAvatarUrl = clone.authorAvatarUrl;
		ticketMessage.messageCreatedAt = clone.messageCreatedAt;
		ticketMessage.messageEditedAt = clone.messageEditedAt;
		ticketMessage.messageDeletedAt = clone.messageDeletedAt;
		ticketMessage.referencesMessageId = clone.referencesMessageId;
		ticketMessage.attachments = new ArrayList<>(clone.attachments);
		ticketMessage.botMessage = clone.botMessage;

		return ticketMessage;
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

			TicketMessage newMessage = TicketMessage.copy(this);
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
	 * Saves the ticket message
	 *
	 * @return True if the ticket message has been saved successfully, false
	 */
	public CompletableFuture<TicketMessage> create() {
		CompletableFuture<TicketMessage> future = new CompletableFuture<>();
		Ticket ticket = getTicket();

		if (ticket == null) {
			future.completeExceptionally(new IllegalStateException("Ticket not found"));
			return future;
		}

		UUID ticketId = ticket.getTicketId();

		if (ticketId == null) {
			future.completeExceptionally(new IllegalStateException("Ticket Id not found"));
			return future;
		}

		TicketMessageService.INSTANCE.createTicketMessage(ticket, this).thenAcceptAsync(future::complete)
									 .exceptionally(exception -> {
										 future.completeExceptionally(exception);
										 return null;
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
		TicketMessage newTicketMessage = TicketMessage.copy(this);

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

			TicketMessageAttachment attachement = new TicketMessageAttachment(newTicketMessage, name, url, extension,
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
	@JsonIgnore
	public Ticket getTicket() {
		return DiscordBot.getInstance().getTicketManager().getTicketById(ticketId);
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
}
