package dev.slne.discord.ticket;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.datasource.DiscordTables;
import dev.slne.discord.guild.DiscordGuild;
import dev.slne.discord.guild.DiscordGuilds;
import dev.slne.discord.ticket.result.TicketCloseResult;
import dev.slne.discord.ticket.result.TicketCreateResult;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@Table(DiscordTables.TICKETS)
public abstract class Ticket extends Model {

    private String ticketId;
    private LocalDateTime openedAt;

    private String guildId;
    private Guild guild;

    private String channelId;
    private TextChannel channel;

    private String ticketTypeString;
    private TicketType ticketType;

    private String ticketAuthorId;
    private User ticketAuthor;

    private String closedById;
    private User closedBy;

    private String closedReason;
    private LocalDateTime closedAt;

    public Ticket(Guild guild, User ticketAuthor, TicketType ticketType) {
        this.ticketId = getRandomTicketId();
        this.openedAt = LocalDateTime.now();

        this.guildId = guild.getId();
        this.guild = guild;

        this.ticketTypeString = ticketType.name();
        this.ticketType = ticketType;

        this.ticketAuthorId = ticketAuthor.getId();
        this.ticketAuthor = ticketAuthor;

        this.closedById = null;
        this.closedBy = null;

        this.closedReason = null;
        this.closedAt = null;
    }

    public abstract void afterOpen();

    public CompletableFuture<TicketCreateResult> createTicketChannel(Guild guild) {
        CompletableFuture<TicketCreateResult> future = new CompletableFuture<>();
        DiscordGuild discordGuild = DiscordGuilds.getGuild(guild);

        if (discordGuild == null) {
            future.complete(TicketCreateResult.GUILD_NOT_FOUND);
            return future;
        }

        Category channelCategory = guild.getCategoryById(discordGuild.getCategoryId());

        if (channelCategory == null) {
            future.completeAsync(() -> TicketCreateResult.CATEGORY_NOT_FOUND);
            return future;
        }

        String ticketName = ticketType.name().toLowerCase() + "-" + ticketAuthor.getName().toLowerCase();
        boolean ticketExists = channelCategory.getChannels().stream()
                .anyMatch(channel -> channel.getName().equalsIgnoreCase(ticketName));

        if (ticketExists) {
            future.completeAsync(() -> TicketCreateResult.ALREADY_EXISTS);
            return future;
        }

        channelCategory.createTextChannel(ticketName).queue(ticketChannel -> {
            this.channelId = ticketChannel.getId();
            this.channel = ticketChannel;

            future.completeAsync(() -> TicketCreateResult.SUCCESS);
        });

        return future;
    }

    public CompletableFuture<TicketCloseResult> closeTicket() {
        CompletableFuture<TicketCloseResult> future = new CompletableFuture<>();

        if (channel == null) {
            future.complete(TicketCloseResult.TICKET_NOT_FOUND);
            return future;
        }

        channel.delete().queue(success -> {
            future.complete(TicketCloseResult.SUCCESS);
        }, error -> {
            future.complete(TicketCloseResult.ERROR);

            System.err.println("Error while closing ticket: " + error.getMessage());
            error.printStackTrace();
        });

        return future;
    }

    @Override
    protected void beforeSave() {
        setString("ticket_id", ticketId);
        setTimestamp("opened_at", openedAt);

        setString("guild_id", guildId);
        setString("channel_id", channelId);

        setString("type", ticketTypeString);

        setString("author_id", ticketAuthorId);
        setString("closed_by_id", closedById);

        setString("closed_reason", closedReason);
        setTimestamp("closed_at", closedAt);
    }

    @Override
    protected void afterLoad() {
        this.ticketId = getString("ticket_id");
        this.openedAt = getTimestamp("opened_at").toLocalDateTime();

        this.guildId = getString("guild_id");
        this.guild = DiscordBot.getInstance().getJda().getGuildById(guildId);
        this.channelId = getString("channel_id");
        this.channel = guild != null ? guild.getTextChannelById(channelId) : null;

        this.ticketTypeString = getString("type");
        try {
            this.ticketType = TicketType.valueOf(ticketTypeString);
        } catch (IllegalArgumentException e) {
            this.ticketType = TicketType.SERVER_SUPPORT;
        }

        this.ticketAuthorId = getString("author_id");
        this.ticketAuthor = guild != null ? guild.getMemberById(ticketAuthorId).getUser() : null;

        this.closedById = getString("closed_by_id");
        this.closedBy = guild != null ? guild.getMemberById(closedById).getUser() : null;

        this.closedReason = getString("closed_reason");
        this.closedAt = getTimestamp("closed_at").toLocalDateTime();
    }

    private String getRandomTicketId() {
        String randomId = "";

        for (int i = 0; i < 10; i++) {
            randomId += (int) (Math.random() * 10);
        }

        return randomId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public User getTicketAuthor() {
        return ticketAuthor;
    }

    public String getTicketAuthorId() {
        return ticketAuthorId;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public String getTicketTypeString() {
        return ticketTypeString;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public String getGuildId() {
        return guildId;
    }

    public Guild getGuild() {
        return guild;
    }

    public String getChannelId() {
        return channelId;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public String getClosedById() {
        return closedById;
    }

    public User getClosedBy() {
        return closedBy;
    }

    public String getClosedReason() {
        return closedReason;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

}
