package dev.slne.discord.ticket;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import dev.slne.data.core.database.future.SurfFutureResult;
import dev.slne.data.core.instance.DataApi;
import dev.slne.discord.Launcher;
import dev.slne.discord.discord.guild.DiscordGuild;
import dev.slne.discord.discord.guild.DiscordGuilds;
import dev.slne.discord.ticket.result.TicketCloseResult;
import dev.slne.discord.ticket.result.TicketCreateResult;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class Ticket {

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

    /**
     * Constructor for a ticket
     *
     * @param guild        The guild the ticket is created in
     * @param ticketAuthor The author of the ticket
     * @param ticketType   The type of the ticket
     */
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

    public void afterOpen() {
        // Implemented by subclasses
    }

    public static <T extends Ticket> CompletableFuture<T> getTicketByChannel(TextChannel channel) {
        return null;
        // return ConnectionWorkers.async(() -> {
        // T ticket = T.findFirst("channel_id = ?", channel.getId());

        // if (ticket == null) {
        // return null;
        // }

        // return ticket;
        // });
    }

    /**
     * Create the ticket channel
     *
     * @param guild The guild the ticket should be created in
     * @return The result of the ticket creation
     */
    public CompletableFuture<TicketCreateResult> createTicketChannel(Guild guild) {
        CompletableFuture<TicketCreateResult> future = new CompletableFuture<>();
        DiscordGuild discordGuild = DiscordGuilds.getGuild(guild);

        if (discordGuild == null) {
            future.complete(TicketCreateResult.GUILD_NOT_FOUND);
            return future;
        }

        String categoryId = discordGuild.getCategoryId();
        if (categoryId == null) {
            future.complete(TicketCreateResult.CATEGORY_NOT_FOUND);
            return future;
        }

        Category channelCategory = guild.getCategoryById(categoryId);

        if (channelCategory == null) {
            future.completeAsync(() -> TicketCreateResult.CATEGORY_NOT_FOUND);
            return future;
        }

        String ticketName = ticketType.name().toLowerCase() + "-" + ticketAuthor.getName().toLowerCase();
        boolean ticketExists = channelCategory.getChannels().stream()
                .anyMatch(categoryChannel -> categoryChannel.getName().equalsIgnoreCase(ticketName));

        if (ticketExists) {
            future.completeAsync(() -> TicketCreateResult.ALREADY_EXISTS);
            return future;
        }

        channelCategory.createTextChannel(ticketName).queue(ticketChannel -> {
            this.channelId = ticketChannel.getId();
            this.channel = ticketChannel;

            saveIt().whenComplete(successOptional -> {
                if (successOptional.isEmpty()) {
                    successOptional = Optional.of(false);
                }

                boolean success = successOptional.get();
                future.completeAsync(() -> success ? TicketCreateResult.SUCCESS : TicketCreateResult.ERROR);

                if (!success) {
                    Launcher.getLogger().logError("Error while saving ticket: " + getChannelId());
                }
            }, throwable -> {
                Launcher.getLogger().logError("Error while saving ticket: " + throwable.getMessage());
                throwable.printStackTrace();

                future.completeExceptionally(throwable);
            });
        });

        return future;
    }

    /**
     * Save the ticket to the database
     *
     * @return The result of the saving
     */
    public SurfFutureResult<Optional<Boolean>> saveIt() {
        return DataApi.getDataInstance().supplyAsync(() -> Optional.of(true));
    }

    public CompletableFuture<TicketCloseResult> closeTicket(User user, String reason) {
        CompletableFuture<TicketCloseResult> future = new CompletableFuture<>();

        if (channel == null) {
            future.complete(TicketCloseResult.TICKET_NOT_FOUND);
            return future;
        }

        this.closedById = user.getId();
        this.closedBy = user;

        this.closedReason = reason;

        channel.delete().queue(success -> {
            future.complete(TicketCloseResult.SUCCESS);

            saveIt();
        }, error -> {
            future.complete(TicketCloseResult.ERROR);

            Launcher.getLogger().logError("Error while closing ticket: " + error.getMessage());
            error.printStackTrace();
        });

        return future;
    }

    /**
     * Get a random ticket id
     *
     * @return The random ticket id
     */
    private String getRandomTicketId() {
        StringBuilder randomIdBuilder = new StringBuilder();
        Random random = Launcher.getRandom();

        for (int i = 0; i < 10; i++) {
            randomIdBuilder.append(random.nextInt(10));
        }

        return randomIdBuilder.toString();
    }

    /**
     * Get the ticket id
     *
     * @return The ticket id
     */
    public String getTicketId() {
        return ticketId;
    }

    /**
     * Get the author of the ticket
     *
     * @return The author of the ticket
     */
    public User getTicketAuthor() {
        return ticketAuthor;
    }

    /**
     * Get the id of the author of the ticket
     *
     * @return The id of the author of the ticket
     */
    public String getTicketAuthorId() {
        return ticketAuthorId;
    }

    /**
     * Get the type of the ticket
     *
     * @return The type of the ticket
     */
    public TicketType getTicketType() {
        return ticketType;
    }

    /**
     * Get the type of the ticket as a string
     *
     * @return The type of the ticket as a string
     */
    public String getTicketTypeString() {
        return ticketTypeString;
    }

    /**
     * Get the opened at date of the ticket
     *
     * @return The opened at date of the ticket
     */
    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    /**
     * Get the id of the guild the ticket is created in
     *
     * @return The id of the guild the ticket is created in
     */
    public String getGuildId() {
        return guildId;
    }

    /**
     * Get the guild the ticket is created in
     *
     * @return The guild the ticket is created in
     */
    public Guild getGuild() {
        return guild;
    }

    /**
     * Get the id of the channel the ticket is created in
     *
     * @return The id of the channel the ticket is created in
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * Get the channel the ticket is created in
     *
     * @return The channel the ticket is created in
     */
    public TextChannel getChannel() {
        return channel;
    }

    /**
     * Get the id of the user that closed the ticket
     *
     * @return The id of the user that closed the ticket
     */
    public String getClosedById() {
        return closedById;
    }

    /**
     * Get the user that closed the ticket
     *
     * @return The user that closed the ticket
     */
    public User getClosedBy() {
        return closedBy;
    }

    /**
     * Get the reason the ticket was closed
     *
     * @return The reason the ticket was closed
     */
    public String getClosedReason() {
        return closedReason;
    }

    /**
     * Get the date the ticket was closed
     *
     * @return The date the ticket was closed
     */
    public LocalDateTime getClosedAt() {
        return closedAt;
    }

}
