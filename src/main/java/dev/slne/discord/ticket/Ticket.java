package dev.slne.discord.ticket;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import dev.slne.data.core.database.future.SurfFutureResult;
import dev.slne.data.core.instance.DataApi;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.Launcher;
import dev.slne.discord.datasource.Times;
import dev.slne.discord.datasource.database.future.DiscordFutureResult;
import dev.slne.discord.discord.guild.DiscordGuild;
import dev.slne.discord.discord.guild.DiscordGuilds;
import dev.slne.discord.ticket.member.TicketMember;
import dev.slne.discord.ticket.message.TicketMessage;
import dev.slne.discord.ticket.result.TicketCloseResult;
import dev.slne.discord.ticket.result.TicketCreateResult;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;

public class Ticket {

    private Optional<Long> id;
    private Optional<String> ticketId;
    private LocalDateTime openedAt;

    private Optional<String> guildId;
    private Optional<Guild> guild;

    private Optional<String> channelId;
    private Optional<TextChannel> channel;

    private String ticketTypeString;
    private TicketType ticketType;

    private String ticketAuthorId;
    private String ticketAuthorName;
    private String ticketAuthorAvatarUrl;
    private RestAction<User> ticketAuthor;

    private Optional<String> closedById;
    private Optional<RestAction<User>> closedBy;

    private Optional<String> closedReason;
    private Optional<LocalDateTime> closedAt;

    private List<TicketMessage> messages;
    private List<TicketMember> members;
    private List<TicketMember> removedMembers;

    private Optional<Webhook> webhook;
    private Optional<String> webhookId;
    private Optional<String> webhookName;
    private Optional<String> webhookUrl;

    /**
     * Constructor for a ticket
     *
     * @param guild        The guild the ticket is created in
     * @param ticketAuthor The author of the ticket
     * @param ticketType   The type of the ticket
     */
    public Ticket(Guild guild, User ticketAuthor, TicketType ticketType) {
        this.id = Optional.empty();
        this.ticketId = Optional.empty();
        this.openedAt = Times.now();

        this.guildId = Optional.of(guild.getId());
        this.guild = Optional.of(guild);

        this.channelId = Optional.empty();
        this.channel = Optional.empty();

        this.ticketTypeString = ticketType.name();
        this.ticketType = ticketType;

        this.ticketAuthorName = ticketAuthor.getName();
        this.ticketAuthorId = ticketAuthor.getId();
        this.ticketAuthor = DiscordBot.getInstance().getJda().retrieveUserById(ticketAuthor.getId());
        this.ticketAuthorAvatarUrl = ticketAuthor.getAvatarUrl();

        this.closedById = Optional.empty();
        this.closedBy = Optional.empty();

        this.closedReason = Optional.empty();
        this.closedAt = Optional.empty();

        this.messages = new ArrayList<>();
        this.members = new ArrayList<>();
        this.removedMembers = new ArrayList<>();

        this.webhook = Optional.empty();
        this.webhookId = Optional.empty();
        this.webhookName = Optional.empty();
        this.webhookUrl = Optional.empty();
    }

    /**
     * Constructor for a ticket
     *
     * @param id                    The id of the ticket
     * @param ticketId              The ticket id
     * @param openedAt              The date the ticket was opened
     * @param guildId               The id of the guild the ticket is created in
     * @param guild                 The guild the ticket is created in
     * @param channelId             The id of the channel the ticket is created in
     * @param channel               The channel the ticket is created in
     * @param ticketTypeString      The type of the ticket as a string
     * @param ticketType            The type of the ticket
     * @param ticketAuthorName      The name of the author of the ticket
     * @param ticketAuthorId        The id of the author of the ticket
     * @param ticketAuthorAvatarUrl The avatar url of the author of the ticket
     * @param ticketAuthor          The author of the ticket
     * @param closedById            The id of the user that closed the ticket
     * @param closedBy              The user that closed the ticket
     * @param closedReason          The reason the ticket was closed
     * @param closedAt              The date the ticket was closed
     * @param messages              The messages of the ticket
     * @param members               The members of the ticket
     */
    @SuppressWarnings("java:S107")
    public Ticket(
            Optional<Long> id, Optional<String> ticketId, LocalDateTime openedAt, Optional<String> guildId,
            Optional<Guild> guild, Optional<String> channelId, Optional<TextChannel> channel, String ticketTypeString,
            TicketType ticketType, String ticketAuthorName, String ticketAuthorId, String ticketAuthorAvatarUrl,
            User ticketAuthor,
            Optional<String> closedById,
            Optional<User> closedBy, Optional<String> closedReason, Optional<LocalDateTime> closedAt,
            List<TicketMessage> messages, List<TicketMember> members, Optional<Webhook> webhook,
            Optional<String> webhookId, Optional<String> webhookName, Optional<String> webhookUrl) {
        this.id = id;
        this.ticketId = ticketId;
        this.openedAt = openedAt;
        this.guildId = guildId;
        this.guild = guild;
        this.channelId = channelId;
        this.channel = channel;
        this.ticketTypeString = ticketTypeString;
        this.ticketType = ticketType;
        this.ticketAuthorAvatarUrl = ticketAuthorAvatarUrl;
        this.ticketAuthorName = ticketAuthorName;
        this.ticketAuthorId = ticketAuthorId;

        if (ticketAuthorId != null) {
            this.ticketAuthor = DiscordBot.getInstance().getJda().retrieveUserById(ticketAuthorId);
        }

        this.closedById = closedById;

        if (closedById.isPresent()) {
            String closedId = closedById.get();

            if (closedId != null) {
                this.closedBy = Optional.of(DiscordBot.getInstance().getJda().retrieveUserById(closedId));
            }
        }

        this.closedReason = closedReason;
        this.closedAt = closedAt;
        this.messages = messages;
        this.members = members;
        this.removedMembers = new ArrayList<>();
        this.webhook = webhook;
        this.webhookId = webhookId;
        this.webhookName = webhookName;
        this.webhookUrl = webhookUrl;
    }

    public void afterOpen() {
        // Implemented by subclasses
    }

    /**
     * Adds a ticket message to the ticket
     *
     * @param ticketMessage The ticket message
     * @return The result of the ticket message adding
     */
    public SurfFutureResult<Optional<TicketMessage>> addTicketMessage(TicketMessage ticketMessage) {
        CompletableFuture<Optional<TicketMessage>> future = new CompletableFuture<>();
        DiscordFutureResult<Optional<TicketMessage>> futureResult = new DiscordFutureResult<>(future);

        ticketMessage.create().whenComplete(ticketMessageCallback -> {
            if (ticketMessageCallback.isEmpty()) {
                future.complete(Optional.empty());
                return;
            }

            addRawTicketMessage(ticketMessage);
            future.complete(ticketMessageCallback);
        });

        return futureResult;
    }

    /**
     * Adds a ticket member to the ticket
     *
     * @param ticketMember The ticket member
     * @param addingUser   The user that adds the ticket member
     * @return The result of the ticket member adding
     */
    public SurfFutureResult<Optional<TicketMember>> addTicketMember(TicketMember ticketMember) {
        CompletableFuture<Optional<TicketMember>> future = new CompletableFuture<>();
        DiscordFutureResult<Optional<TicketMember>> futureResult = new DiscordFutureResult<>(future);

        RestAction<User> userRest = ticketMember.getMember().orElse(null);

        if (userRest == null) {
            future.complete(Optional.empty());
            return futureResult;
        }

        userRest.queue(user -> {
            if (user == null || memberExists(user)) {
                future.complete(Optional.empty());
                return;
            }

            ticketMember.create().whenComplete(ticketMemberCallback -> {
                if (ticketMemberCallback.isEmpty()) {
                    future.complete(Optional.empty());
                    return;
                }

                addRawTicketMember(ticketMember);
                TicketChannel.updateChannelPermissions(this);

                future.complete(ticketMemberCallback);
            });
        });

        return futureResult;
    }

    /**
     * Removes a ticket member from the ticket
     *
     * @param ticketMember The ticket member
     * @return The result of the ticket member removing
     */
    public SurfFutureResult<Optional<TicketMember>> removeTicketMember(TicketMember ticketMember) {
        CompletableFuture<Optional<TicketMember>> future = new CompletableFuture<>();
        DiscordFutureResult<Optional<TicketMember>> futureResult = new DiscordFutureResult<>(future);

        ticketMember.delete().whenComplete(ticketMemberCallback -> {
            if (ticketMemberCallback.isEmpty()) {
                future.complete(Optional.empty());
                return;
            }

            removeRawTicketMember(ticketMember);
            TicketChannel.updateChannelPermissions(this);

            future.complete(ticketMemberCallback);
        });

        return futureResult;
    }

    /**
     * Adds a raw ticket member to the ticket
     *
     * @param ticketMember The ticket member
     */
    public void addRawTicketMember(TicketMember ticketMember) {
        members.add(ticketMember);
    }

    /**
     * Removes a raw ticket member from the ticket
     *
     * @param ticketMember The ticket member
     */
    public void removeRawTicketMember(TicketMember ticketMember) {
        members.remove(ticketMember);
        removedMembers.add(ticketMember);
    }

    /**
     * Adds a raw ticket message to the ticket
     *
     * @param ticketMessage The ticket message
     */
    public void addRawTicketMessage(TicketMessage ticketMessage) {
        messages.add(ticketMessage);
    }

    /**
     * Update the members of the ticket
     */
    public void initialMembers() {
        if (guild.isEmpty()) {
            return;
        }

        User artyUser = DiscordBot.getInstance().getJda().getSelfUser();
        addTicketMember(new TicketMember(this, artyUser, artyUser)).join();

        User author = ticketAuthor.complete();
        TicketMember authorMember = new TicketMember(this, author, DiscordBot.getInstance().getJda().getSelfUser());
        addTicketMember(authorMember).join();

        DiscordGuild discordGuild = DiscordGuilds.getGuild(guild.get());

        for (User user : discordGuild.getAllUsers()) {
            TicketMember member = new TicketMember(this, user, artyUser);
            addTicketMember(member).join();
        }
    }

    /**
     * Check if the member exists
     *
     * @param user The user to check
     * @return If the member exists
     */
    private boolean memberExists(User user) {
        return members.stream().anyMatch(member -> member.getMemberId().equals(user.getId()) && !member.isRemoved());
    }

    /**
     * Opens the ticket from the button
     *
     * @return The result of the ticket opening
     */
    public SurfFutureResult<TicketCreateResult> openFromButton() {
        CompletableFuture<TicketCreateResult> future = new CompletableFuture<>();
        DiscordFutureResult<TicketCreateResult> futureResult = new DiscordFutureResult<>(future);

        DataApi.getDataInstance()
                .runAsync(() -> TicketChannel.createTicketChannel(this)
                        .whenComplete(ticketChannelCreateResultOptional -> {
                            if (ticketChannelCreateResultOptional.isEmpty()) {
                                future.complete(TicketCreateResult.ERROR);
                                return;
                            }

                            TicketCreateResult ticketChannelCreateResult = ticketChannelCreateResultOptional.get();

                            if (ticketChannelCreateResult != TicketCreateResult.SUCCESS) {
                                future.complete(ticketChannelCreateResult);
                                return;
                            }

                            TicketRepository.createTicket(this).whenComplete(ticketCreateResultOptional -> {
                                if (ticketCreateResultOptional.isEmpty()) {
                                    future.complete(TicketCreateResult.ERROR);
                                    return;
                                }

                                initialMembers();
                                TicketChannel.updateChannelPermissions(this);
                                afterOpen();

                                future.complete(TicketCreateResult.SUCCESS);

                                DiscordBot.getInstance().getTicketManager().addTicket(this);
                            });
                        }));

        return futureResult;
    }

    /**
     * Open the ticket channel from pusher
     *
     * @return The result of the ticket opening
     */
    public SurfFutureResult<TicketCreateResult> openFromPusher() {
        CompletableFuture<TicketCreateResult> future = new CompletableFuture<>();
        DiscordFutureResult<TicketCreateResult> futureResult = new DiscordFutureResult<>(future);

        DataApi.getDataInstance()
                .runAsync(() -> TicketChannel.createTicketChannel(this).whenComplete(ticketCreateResultOptional -> {
                    if (ticketCreateResultOptional.isEmpty()) {
                        future.complete(TicketCreateResult.ERROR);
                        return;
                    }

                    TicketCreateResult ticketCreateResult = ticketCreateResultOptional.get();

                    if (ticketCreateResult != TicketCreateResult.SUCCESS) {
                        future.complete(ticketCreateResult);
                        return;
                    }

                    TicketChannel.updateChannelPermissions(this);
                    afterOpen();
                    printAllPreviousMessages();

                    DiscordBot.getInstance().getTicketManager().addTicket(this);
                    future.complete(ticketCreateResult);
                }));

        return futureResult;
    }

    /**
     * Print all previous messages
     */
    private void printAllPreviousMessages() {
        for (TicketMessage message : messages) {
            message.printMessage();
        }
    }

    /**
     * Close the ticket channel
     *
     * @param user   The user that closed the ticket
     * @param reason The reason the ticket was closed
     * @return The result of the ticket closing
     */
    public SurfFutureResult<TicketCloseResult> close(User user, String reason) {
        CompletableFuture<TicketCloseResult> future = new CompletableFuture<>();
        DiscordFutureResult<TicketCloseResult> futureResult = new DiscordFutureResult<>(future);

        DataApi.getDataInstance().runAsync(() -> {
            if (channel.isEmpty()) {
                future.complete(TicketCloseResult.TICKET_NOT_FOUND);
                return;
            }

            this.closedById = Optional.of(user.getId());
            this.closedBy = Optional.of(DiscordBot.getInstance().getJda().retrieveUserById(user.getId()));

            this.closedReason = Optional.of(reason);

            TicketRepository.closeTicket(this).whenComplete(newTicketOptional -> {
                if (newTicketOptional.isEmpty()) {
                    future.complete(TicketCloseResult.ERROR);
                    return;
                }

                TicketChannel.deleteTicketChannel(this).whenComplete(v -> {
                    future.complete(TicketCloseResult.SUCCESS);
                    DiscordBot.getInstance().getTicketManager().removeTicket(this);
                }, throwable -> {
                    future.complete(TicketCloseResult.ERROR);
                    Launcher.getLogger().logError("Error while closing ticket: " + throwable.getMessage());
                    throwable.printStackTrace();
                });
            }, throwable -> {
                future.complete(TicketCloseResult.ERROR);

                Launcher.getLogger().logError("Error while closing ticket: " + throwable.getMessage());
                throwable.printStackTrace();
            });
        });

        return futureResult;
    }

    /**
     * Returns the ticket message by the message
     *
     * @param message The message
     * @return The ticket message
     */
    public Optional<TicketMessage> getTicketMessage(Message message) {
        return messages.stream().filter(ticketMessage -> ticketMessage.getMessageId().equals(message.getId()))
                .findFirst();
    }

    /**
     * Returns the ticket message by the message id
     *
     * @param messageId The message id
     * @return The ticket message
     */
    public Optional<TicketMessage> getTicketMessage(String messageId) {
        return messages.stream().filter(ticketMessage -> ticketMessage.getMessageId().equals(messageId)).findFirst();
    }

    /**
     * Returns the ticket member by the member
     *
     * @param user The member
     * @return The ticket member
     */
    public Optional<TicketMember> getTicketMember(User user) {
        return members.stream().filter(ticketMember -> ticketMember.getMemberId().equals(user.getId())).findFirst();
    }

    /**
     * Returns the active ticket member by the member
     *
     * @param user The member
     * @return The active ticket member
     */
    public Optional<TicketMember> getActiveTicketMember(User user) {
        return members.stream()
                .filter(ticketMember -> ticketMember.getMemberId().equals(user.getId()) && !ticketMember.isRemoved())
                .findFirst();
    }

    /**
     * Returns the ticket member by the member id
     *
     * @param userId The member id
     * @return The ticket member
     */
    public Optional<TicketMember> getTicketMember(String userId) {
        return members.stream().filter(ticketMember -> ticketMember.getMemberId().equals(userId)).findFirst();
    }

    /**
     * Get the ticket id
     *
     * @return The ticket id
     */
    public Optional<String> getTicketId() {
        return ticketId;
    }

    /**
     * @return the ticketAuthor
     */
    public RestAction<User> getTicketAuthor() {
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
    public Optional<String> getGuildId() {
        return guildId;
    }

    /**
     * Get the guild the ticket is created in
     *
     * @return The guild the ticket is created in
     */
    public Optional<Guild> getGuild() {
        return guild;
    }

    /**
     * Get the id of the channel the ticket is created in
     *
     * @return The id of the channel the ticket is created in
     */
    public Optional<String> getChannelId() {
        return channelId;
    }

    /**
     * Get the channel the ticket is created in
     *
     * @return The channel the ticket is created in
     */
    public Optional<TextChannel> getChannel() {
        return channel;
    }

    /**
     * Get the id of the user that closed the ticket
     *
     * @return The id of the user that closed the ticket
     */
    public Optional<String> getClosedById() {
        return closedById;
    }

    /**
     * @return the closedBy
     */
    public Optional<RestAction<User>> getClosedBy() {
        return closedBy;
    }

    /**
     * Get the reason the ticket was closed
     *
     * @return The reason the ticket was closed
     */
    public Optional<String> getClosedReason() {
        return closedReason;
    }

    /**
     * Get the date the ticket was closed
     *
     * @return The date the ticket was closed
     */
    public Optional<LocalDateTime> getClosedAt() {
        return closedAt;
    }

    /**
     * Returns the id of the ticket
     *
     * @return the id
     */
    public Optional<Long> getId() {
        return id;
    }

    /**
     * @return the messages
     */
    public List<TicketMessage> getMessages() {
        return messages;
    }

    /**
     * @return the members
     */
    public List<TicketMember> getMembers() {
        return members;
    }

    /**
     * @return the ticketAuthorAvatarUrl
     */
    public String getTicketAuthorAvatarUrl() {
        return ticketAuthorAvatarUrl;
    }

    /**
     * @return the ticketAuthorName
     */
    public String getTicketAuthorName() {
        return ticketAuthorName;
    }

    /**
     * @param id the id to set
     */
    public void setId(Optional<Long> id) {
        this.id = id;
    }

    /**
     * @param ticketId the ticketId to set
     */
    public void setTicketId(Optional<String> ticketId) {
        this.ticketId = ticketId;
    }

    /**
     * @param openedAt the openedAt to set
     */
    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    /**
     * @param messages the messages to set
     */
    public void setMessages(List<TicketMessage> messages) {
        this.messages = messages;
    }

    /**
     * @param members the members to set
     */
    public void setMembers(List<TicketMember> members) {
        this.members = members;
    }

    /**
     * @param closedAt the closedAt to set
     */
    public void setClosedAt(Optional<LocalDateTime> closedAt) {
        this.closedAt = closedAt;
    }

    /**
     * @param channel the channel to set
     */
    public void setChannel(Optional<TextChannel> channel) {
        this.channel = channel;
    }

    /**
     * @param channelId the channelId to set
     */
    public void setChannelId(Optional<String> channelId) {
        this.channelId = channelId;
    }

    /**
     * @return the removedMembers
     */
    public List<TicketMember> getRemovedMembers() {
        return removedMembers;
    }

    /**
     * @return the webhook
     */
    public Optional<Webhook> getWebhook() {
        return webhook;
    }

    /**
     * @return the webhookId
     */
    public Optional<String> getWebhookId() {
        return webhookId;
    }

    /**
     * @return the webhookName
     */
    public Optional<String> getWebhookName() {
        return webhookName;
    }

    /**
     * @return the webhookUrl
     */
    public Optional<String> getWebhookUrl() {
        return webhookUrl;
    }

    /**
     * @param webhook the webhook to set
     */
    public void setWebhook(Optional<Webhook> webhook) {
        this.webhook = webhook;

        if (webhook.isEmpty()) {
            return;
        }

        Webhook webhookItem = webhook.get();
        this.webhookId = Optional.of(webhookItem.getId());
        this.webhookName = Optional.of(webhookItem.getName());
        this.webhookUrl = Optional.of(webhookItem.getUrl());
    }

    /**
     * @param closedBy the closedBy to set
     */
    public void setClosedBy(Optional<RestAction<User>> closedBy) {
        this.closedBy = closedBy;
    }

    /**
     * @param closedById the closedById to set
     */
    public void setClosedById(Optional<String> closedById) {
        this.closedById = closedById;
    }

    /**
     * @param closedReason the closedReason to set
     */
    public void setClosedReason(Optional<String> closedReason) {
        this.closedReason = closedReason;
    }

    /**
     * @param guild the guild to set
     */
    public void setGuild(Optional<Guild> guild) {
        this.guild = guild;
    }

    /**
     * @param guildId the guildId to set
     */
    public void setGuildId(Optional<String> guildId) {
        this.guildId = guildId;
    }

    /**
     * @param removedMembers the removedMembers to set
     */
    public void setRemovedMembers(List<TicketMember> removedMembers) {
        this.removedMembers = removedMembers;
    }

    /**
     * @param ticketAuthor the ticketAuthor to set
     */
    public void setTicketAuthor(RestAction<User> ticketAuthor) {
        this.ticketAuthor = ticketAuthor;
    }

    /**
     * @param ticketAuthorAvatarUrl the ticketAuthorAvatarUrl to set
     */
    public void setTicketAuthorAvatarUrl(String ticketAuthorAvatarUrl) {
        this.ticketAuthorAvatarUrl = ticketAuthorAvatarUrl;
    }

    /**
     * @param ticketAuthorId the ticketAuthorId to set
     */
    public void setTicketAuthorId(String ticketAuthorId) {
        this.ticketAuthorId = ticketAuthorId;
    }

    /**
     * @param ticketAuthorName the ticketAuthorName to set
     */
    public void setTicketAuthorName(String ticketAuthorName) {
        this.ticketAuthorName = ticketAuthorName;
    }

    /**
     * @param ticketType the ticketType to set
     */
    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    /**
     * @param ticketTypeString the ticketTypeString to set
     */
    public void setTicketTypeString(String ticketTypeString) {
        this.ticketTypeString = ticketTypeString;
    }

    /**
     * @param webhookId the webhookId to set
     */
    public void setWebhookId(Optional<String> webhookId) {
        this.webhookId = webhookId;
    }

    /**
     * @param webhookName the webhookName to set
     */
    public void setWebhookName(Optional<String> webhookName) {
        this.webhookName = webhookName;
    }

    /**
     * @param webhookUrl the webhookUrl to set
     */
    public void setWebhookUrl(Optional<String> webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

}
