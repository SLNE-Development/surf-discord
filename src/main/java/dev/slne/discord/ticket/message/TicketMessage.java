package dev.slne.discord.ticket.message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.HasMany;
import org.javalite.activejdbc.annotations.Table;

import dev.slne.discord.datasource.DiscordTables;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.User;

@Table(DiscordTables.MESSAGES)
@HasMany(child = TicketMessageAttachement.class, foreignKeyName = "message_id")
public class TicketMessage extends Model {

    private Message message;
    private String messageId;

    private String authorId;
    private User author;

    private LocalDateTime messageCreatedAt;
    private LocalDateTime messageEditedAt;
    private LocalDateTime messageDeletedAt;

    private String referencesMessageId;

    private List<TicketMessageAttachement> attachments;

    public TicketMessage() {

    }

    public TicketMessage(Message message) {
        this.message = message;
        this.messageId = message.getId();

        this.author = message.getAuthor();
        this.authorId = this.author.getId();

        this.messageCreatedAt = message.getTimeCreated().toLocalDateTime();
        this.messageEditedAt = message.isEdited() ? message.getTimeEdited().toLocalDateTime() : null;

        MessageReference reference = message.getMessageReference();
        reference.resolve().queue(referencedMessage -> {
            this.referencesMessageId = referencedMessage.getId();
        });

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
            add(attachement);
        }
    }

    @Override
    protected void beforeSave() {
        setString("message_id", messageId);

        setString("author_id", authorId);

        setTimestamp("message_created_at", messageCreatedAt);
        setTimestamp("message_edited_at", messageEditedAt);
        setTimestamp("message_deleted_at", messageDeletedAt);

        setString("references_message_id", referencesMessageId);
    }

    @Override
    protected void afterLoad() {
        this.messageId = getString("message_id");

        this.authorId = getString("author_id");

        this.messageCreatedAt = getTimestamp("message_created_at").toLocalDateTime();
        this.messageEditedAt = getTimestamp("message_edited_at").toLocalDateTime();
        this.messageDeletedAt = getTimestamp("message_deleted_at").toLocalDateTime();

        this.referencesMessageId = getString("references_message_id");

        this.attachments = getAll(TicketMessageAttachement.class);
    }

    public Message getMessage() {
        return message;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public User getAuthor() {
        return author;
    }

    public LocalDateTime getMessageCreatedAt() {
        return messageCreatedAt;
    }

    public LocalDateTime getMessageEditedAt() {
        return messageEditedAt;
    }

    public LocalDateTime getMessageDeletedAt() {
        return messageDeletedAt;
    }

    public String getReferencesMessageId() {
        return referencesMessageId;
    }

    public List<TicketMessageAttachement> getAttachments() {
        return attachments;
    }

    @Override
    public boolean save() {
        boolean result = super.save();

        for (TicketMessageAttachement attachment : attachments) {
            result &= attachment.saveIt();
        }

        return result;
    }

    @Override
    public boolean saveIt() {
        boolean result = super.saveIt();

        for (TicketMessageAttachement attachment : attachments) {
            result &= attachment.saveIt();
        }

        return result;
    }

}
