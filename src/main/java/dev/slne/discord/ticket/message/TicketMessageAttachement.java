package dev.slne.discord.ticket.message;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.BelongsTo;
import org.javalite.activejdbc.annotations.Table;

import dev.slne.discord.datasource.DiscordTables;

@Table(DiscordTables.MESSAGE_ATTACHMENTS)
@BelongsTo(parent = TicketMessage.class, foreignKeyName = "message_id")
public class TicketMessageAttachement extends Model {

    private TicketMessage message;

    private String name;
    private String url;
    private String extension;
    private int size;

    private String description;

    public TicketMessageAttachement(TicketMessage message, String name, String url, String extension, int size,
            String description) {
        this.message = message;

        this.name = name;
        this.url = url;
        this.extension = extension;
        this.size = size;
        this.description = description;
    }

    @Override
    protected void afterLoad() {
        this.name = getString("name");
        this.url = getString("url");
        this.extension = getString("extension");
        this.size = getInteger("size");
        this.description = getString("description");
    }

    @Override
    protected void beforeSave() {
        setString("name", name);
        setString("url", url);
        setString("extension", extension);
        setInteger("size", size);
        setString("description", description);
    }

    public TicketMessage getMessage() {
        if (message == null) {
            message = parent(TicketMessage.class);
        }

        return message;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getExtension() {
        return extension;
    }

    public int getSize() {
        return size;
    }

    public String getDescription() {
        return description;
    }

}
