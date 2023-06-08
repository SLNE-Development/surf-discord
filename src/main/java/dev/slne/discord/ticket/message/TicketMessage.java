package dev.slne.discord.ticket.message;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.User;

public class TicketMessage {

    private Message message;
    private String messageId;

    private String authorId;
    private User author;

    private LocalDateTime messageCreatedAt;
    private LocalDateTime messageEditedAt;
    private LocalDateTime messageDeletedAt;

    private String referencesMessageId;

    private List<TicketMessageAttachement> attachments;

    /**
     * Constructor for a ticket message
     *
     * @param message The message to create the ticket message from
     */
    public TicketMessage(Message message) {
        this.message = message;
        this.messageId = message.getId();

        this.author = message.getAuthor();
        this.authorId = this.author.getId();

        this.messageCreatedAt = message.getTimeCreated().toLocalDateTime();

        OffsetDateTime timeEdited = message.getTimeEdited();
        this.messageEditedAt = message.isEdited() && timeEdited != null ? timeEdited.toLocalDateTime() : null;

        MessageReference reference = message.getMessageReference();
        if (reference != null) {
            reference.resolve().queue(referencedMessage -> this.referencesMessageId = referencedMessage.getId());
        }

        this.attachments = new ArrayList<>();
        for (Attachment attachment : message.getAttachments()) {
            String name = attachment.getFileName();
            String url = attachment.getUrl();
            String extension = attachment.getFileExtension();
            int size = attachment.getSize();

            String description = attachment.getDescription();

            TicketMessageAttachement attachement = new TicketMessageAttachement(this, name, url, extension, size,
                    description);
            this.attachments.add(attachement);
        }
    }

    /**
     * Returns the message of the ticket message
     *
     * @return The message of the ticket message
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Returns the id of the ticket message
     *
     * @return The id of the ticket message
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Returns the id of the author of the ticket message
     *
     * @return The id of the author of the ticket message
     */
    public String getAuthorId() {
        return authorId;
    }

    /**
     * Returns the author of the ticket message
     *
     * @return The author of the ticket message
     */
    public User getAuthor() {
        return author;
    }

    /**
     * Returns the creation date of the ticket message
     *
     * @return The creation date of the ticket message
     */
    public LocalDateTime getMessageCreatedAt() {
        return messageCreatedAt;
    }

    /**
     * Returns the date of the last edit of the ticket message
     *
     * @return The date of the last edit of the ticket message
     */
    public LocalDateTime getMessageEditedAt() {
        return messageEditedAt;
    }

    /**
     * Returns the date of the deletion of the ticket message
     *
     * @return The date of the deletion of the ticket message
     */
    public LocalDateTime getMessageDeletedAt() {
        return messageDeletedAt;
    }

    /**
     * Returns the id of the referenced message of the ticket message
     *
     * @return The id of the referenced message of the ticket message
     */
    public String getReferencesMessageId() {
        return referencesMessageId;
    }

    /**
     * Returns the attachments of the ticket message
     *
     * @return The attachments of the ticket message
     */
    public List<TicketMessageAttachement> getAttachments() {
        return attachments;
    }

    // @Override
    // public boolean save() {
    // boolean result = super.save();

    // for (TicketMessageAttachement attachment : attachments) {
    // result &= attachment.saveIt();
    // }

    // return result;
    // }

    // @Override
    // public boolean saveIt() {
    // boolean result = super.saveIt();

    // for (TicketMessageAttachement attachment : attachments) {
    // result &= attachment.saveIt();
    // }

    // return result;
    // }

}
