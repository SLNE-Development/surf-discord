package dev.slne.discord.ticket;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import dev.slne.data.core.database.future.SurfFutureResult;
import dev.slne.data.core.gson.GsonConverter;
import dev.slne.data.core.instance.DataApi;
import dev.slne.data.core.web.WebRequest;
import dev.slne.data.core.web.WebResponse;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.Launcher;
import dev.slne.discord.datasource.API;
import dev.slne.discord.datasource.database.future.DiscordFutureResult;
import dev.slne.discord.discord.guild.DiscordGuild;
import dev.slne.discord.discord.guild.DiscordGuilds;
import dev.slne.discord.discord.guild.GuildRole;
import dev.slne.discord.ticket.member.TicketMember;
import dev.slne.discord.ticket.message.TicketMessage;
import dev.slne.discord.ticket.result.TicketCloseResult;
import dev.slne.discord.ticket.result.TicketCreateResult;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;

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
    private User ticketAuthor;

    private Optional<String> closedById;
    private Optional<User> closedBy;

    private Optional<String> closedReason;
    private Optional<LocalDateTime> closedAt;

    private List<TicketMessage> messages;
    private List<TicketMember> members;

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
        this.openedAt = LocalDateTime.now();

        this.guildId = Optional.of(guild.getId());
        this.guild = Optional.of(guild);

        this.channelId = Optional.empty();
        this.channel = Optional.empty();

        this.ticketTypeString = ticketType.name();
        this.ticketType = ticketType;

        this.ticketAuthorName = ticketAuthor.getName();
        this.ticketAuthorId = ticketAuthor.getId();
        this.ticketAuthor = ticketAuthor;
        this.ticketAuthorAvatarUrl = ticketAuthor.getAvatarUrl();

        this.closedById = Optional.empty();
        this.closedBy = Optional.empty();

        this.closedReason = Optional.empty();
        this.closedAt = Optional.empty();

        this.messages = new ArrayList<>();
        this.members = new ArrayList<>();
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
            List<TicketMessage> messages, List<TicketMember> members) {
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
        this.ticketAuthor = ticketAuthor;
        this.closedById = closedById;
        this.closedBy = closedBy;
        this.closedReason = closedReason;
        this.closedAt = closedAt;
        this.messages = messages;
        this.members = members;
    }

    public void afterOpen() {
        // Implemented by subclasses
    }

    /**
     * Get the ticket by the id
     *
     * @param <T>     The type of the ticket
     * @param channel The channel the ticket is created in
     * @return The ticket
     */
    @SuppressWarnings({ "java:S1192", "java:S1141" })
    public static Optional<Ticket> getTicketByChannel(String channelId) {
        return DiscordBot.getInstance().getTicketManager().getTicket(channelId);
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
     * @return The result of the ticket member adding
     */
    public SurfFutureResult<Optional<TicketMember>> addTicketMember(TicketMember ticketMember) {
        CompletableFuture<Optional<TicketMember>> future = new CompletableFuture<>();
        DiscordFutureResult<Optional<TicketMember>> futureResult = new DiscordFutureResult<>(future);

        User user = ticketMember.getMember().orElse(null);

        if (user == null || memberExists(user)) {
            future.complete(Optional.empty());
            return futureResult;
        }

        ticketMember.create().whenComplete(ticketMemberCallback -> {
            if (ticketMemberCallback.isEmpty()) {
                future.complete(Optional.empty());
                return;
            }

            addRawTicketMember(ticketMember);
            future.complete(ticketMemberCallback);
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
     * Adds a raw ticket message to the ticket
     *
     * @param ticketMessage The ticket message
     */
    public void addRawTicketMessage(TicketMessage ticketMessage) {
        messages.add(ticketMessage);
    }

    /**
     * Get the active tickets
     *
     * @return The active tickets
     */
    public static SurfFutureResult<Optional<List<Ticket>>> getActiveTickets() {
        return DataApi.getDataInstance().supplyAsync(() -> {
            WebRequest request = WebRequest.builder().url(API.ACTIVE_TICKETS).build();
            WebResponse response = request.executeGet().join();
            List<Ticket> tickets = new ArrayList<>();

            if (response.getStatusCode() != 200) {
                return Optional.of(tickets);
            }

            Object body = response.getBody();
            String bodyString = body.toString();

            GsonConverter gson = new GsonConverter();
            JsonObject bodyElement = gson.fromJson(bodyString, JsonObject.class);

            if (!bodyElement.has("data") || !bodyElement.get("data").isJsonArray()) {
                return Optional.of(tickets);
            }

            JsonArray dataArray = (JsonArray) bodyElement.get("data");

            for (JsonElement ticketElement : dataArray) {
                if (!ticketElement.isJsonObject()) {
                    continue;
                }

                JsonObject ticketObject = ticketElement.getAsJsonObject();
                Ticket ticket = ticketByJson(ticketObject);

                tickets.add(ticket);
            }
            return Optional.of(tickets);
        });
    }

    /**
     * Returns the ticket by the jsonobject
     *
     * @param jsonObject The json object
     * @return The ticket
     */
    @SuppressWarnings("java:S3776")
    private static Ticket ticketByJson(JsonObject jsonObject) {
        Optional<Long> id = Optional.empty();
        if (jsonObject.has("id")) {
            id = Optional.of(jsonObject.get("id").getAsLong());
        }

        Optional<String> ticketId = Optional.empty();
        if (jsonObject.has("ticket_id")) {
            ticketId = Optional.of(jsonObject.get("ticket_id").getAsString());
        }

        LocalDateTime openedAt = null;
        if (jsonObject.has("opened_at")) {
            openedAt = LocalDateTime.parse(jsonObject.get("opened_at").getAsString().split("\\.")[0]);
        }

        Optional<String> guildId = Optional.empty();
        Optional<Guild> guild = Optional.empty();
        if (jsonObject.has("guild_id") && jsonObject.get("guild_id") != null && !(jsonObject
                .get("guild_id") instanceof JsonNull)) {
            String guildIdString = jsonObject.get("guild_id").getAsString() + "";
            guildId = Optional.of(guildIdString);
            guild = Optional.ofNullable(DiscordBot.getInstance().getJda().getGuildById(guildIdString));
        }

        Optional<String> channelId = Optional.empty();
        Optional<TextChannel> channel = Optional.empty();
        if (jsonObject.has("channel_id") && jsonObject.get("channel_id") != null && !(jsonObject
                .get("channel_id") instanceof JsonNull)) {
            String channelIdString = jsonObject.get("channel_id").getAsString() + "";
            channelId = Optional.of(channelIdString);
            channel = Optional.ofNullable(DiscordBot.getInstance().getJda().getTextChannelById(channelIdString));
        }

        String ticketTypeString = null;
        TicketType ticketType = null;
        if (jsonObject.has("type")) {
            ticketTypeString = jsonObject.get("type").getAsString();
            ticketType = TicketType.getByName(ticketTypeString);
        }

        String ticketAuthorName = null;
        String ticketAuthorId = null;
        String ticketAuthorAvatarUrl = null;
        User ticketAuthor = null;
        if (jsonObject.has("author_id") && jsonObject.get("author_id") != null && !(jsonObject
                .get("author_id") instanceof JsonNull)) {
            ticketAuthorId = jsonObject.get("author_id").getAsString() + "";
            ticketAuthor = DiscordBot.getInstance().getJda().getUserById(ticketAuthorId);
        }

        if (jsonObject.has("author_name") && jsonObject.get("author_name") != null && !(jsonObject
                .get("author_name") instanceof JsonNull)) {
            ticketAuthorName = jsonObject.get("author_name").getAsString() + "";
        }

        if (jsonObject.has("author_avatar_url") && jsonObject.get("author_avatar_url") != null && !(jsonObject
                .get("author_avatar_url") instanceof JsonNull)) {
            ticketAuthorAvatarUrl = jsonObject.get("author_avatar_url").getAsString() + "";
        }

        Optional<String> closedById = Optional.empty();
        Optional<User> closedBy = Optional.empty();
        if (jsonObject.has("closed_by_id") && jsonObject.get("closed_by_id") != null && !(jsonObject
                .get("closed_by_id") instanceof JsonNull)) {
            String closedByIdString = jsonObject.get("closed_by_id").getAsString() + "";
            closedById = Optional.of(closedByIdString);
            closedBy = Optional.ofNullable(DiscordBot.getInstance().getJda().getUserById(closedByIdString));
        }

        Optional<String> closedReason = Optional.empty();
        if (jsonObject.has("closed_reason") && jsonObject.get("closed_reason") != null && !(jsonObject
                .get("closed_reason") instanceof JsonNull)) {
            closedReason = Optional.of(jsonObject.get("closed_reason").getAsString());
        }

        Optional<LocalDateTime> closedAt = Optional.empty();
        if (jsonObject.has("closed_at") && jsonObject.get("closed_at") != null && !(jsonObject
                .get("closed_at") instanceof JsonNull)) {
            closedAt = Optional.of(LocalDateTime.parse(jsonObject.get("closed_at").getAsString().split("\\.")[0]));
        }

        List<TicketMessage> messages = new ArrayList<>();
        List<TicketMember> members = new ArrayList<>();

        Ticket ticket = new Ticket(id, ticketId, openedAt, guildId, guild, channelId, channel, ticketTypeString,
                ticketType, ticketAuthorName, ticketAuthorId, ticketAuthorAvatarUrl, ticketAuthor, closedById, closedBy,
                closedReason,
                closedAt, messages,
                members);

        if (jsonObject.has("messages")) {
            JsonArray messagesArray = jsonObject.get("messages").getAsJsonArray();

            for (JsonElement messageElement : messagesArray) {
                if (!messageElement.isJsonObject()) {
                    continue;
                }

                JsonObject messageObject = messageElement.getAsJsonObject();
                TicketMessage message = TicketMessage.fromJsonObject(ticket, messageObject);

                messages.add(message);
            }
        }

        if (jsonObject.has("members")) {
            JsonArray membersArray = jsonObject.get("members").getAsJsonArray();

            for (JsonElement memberElement : membersArray) {
                if (!memberElement.isJsonObject()) {
                    continue;
                }

                JsonObject memberObject = memberElement.getAsJsonObject();
                TicketMember member = TicketMember.fromJsonObject(ticket, memberObject);

                members.add(member);
            }
        }

        ticket.messages = messages;
        ticket.members = members;

        return ticket;
    }

    /**
     * Returns a map of the parameters
     *
     * @return The map of the parameters
     */
    private Map<String, String> toParameters() {
        Map<String, String> parameters = new HashMap<>();

        parameters.put("author_id", ticketAuthorId);
        parameters.put("author_name", ticketAuthorName);
        parameters.put("author_avatar_url", ticketAuthorAvatarUrl);
        parameters.put("type", ticketTypeString);
        parameters.put("channel_id", channelId.orElse(""));
        parameters.put("guild_id", guildId.orElse(""));

        parameters.put("closed_by_id", closedById.orElse(""));
        parameters.put("closed_reason", closedReason.orElse(""));

        return parameters;
    }

    /**
     * Save the ticket
     *
     * @return The result of the ticket saving
     */
    public SurfFutureResult<Optional<Ticket>> createTicket() {
        return DataApi.getDataInstance().supplyAsync(() -> {
            WebRequest request = WebRequest.builder().url(API.TICKETS).json(true).parameters(toParameters()).build();
            WebResponse response = request.executePost().join();

            if (response.getStatusCode() != 201) {
                Launcher.getLogger().logError(response.getBody());
                return Optional.empty();
            }

            Object body = response.getBody();
            String bodyString = body.toString();

            GsonConverter gson = new GsonConverter();
            JsonObject bodyElement = gson.fromJson(bodyString, JsonObject.class);

            if (!bodyElement.has("data") || !bodyElement.get("data").isJsonObject()) {
                return Optional.empty();
            }

            JsonObject jsonObject = (JsonObject) bodyElement.get("data");
            Ticket newTicket = ticketByJson(jsonObject);

            openedAt = newTicket.openedAt;
            ticketId = newTicket.ticketId;
            id = newTicket.id;

            DiscordBot.getInstance().getTicketManager().addTicket(newTicket);

            return Optional.of(this);
        });
    }

    /**
     * Create the ticket channel
     *
     * @param guild The guild the ticket should be created in
     * @return The result of the ticket creation
     */
    public SurfFutureResult<Optional<TicketCreateResult>> createTicketChannel(Guild guild) {
        return DataApi.getDataInstance().supplyAsync(() -> {
            DiscordGuild discordGuild = DiscordGuilds.getGuild(guild);

            if (discordGuild == null) {
                return Optional.of(TicketCreateResult.GUILD_NOT_FOUND);
            }

            String categoryId = discordGuild.getCategoryId();
            if (categoryId == null) {
                return Optional.of(TicketCreateResult.CATEGORY_NOT_FOUND);
            }

            Category channelCategory = guild.getCategoryById(categoryId);

            if (channelCategory == null) {
                return Optional.of(TicketCreateResult.CATEGORY_NOT_FOUND);
            }
            String ticketTypeName = ticketType.name().toLowerCase();
            String authorName = ticketAuthor.getName().toLowerCase().trim().replace(" ", "-");

            int maxLength = Channel.MAX_NAME_LENGTH;
            String ticketName = ticketTypeName + "-" + authorName;

            if (ticketName.length() > maxLength) {
                ticketName = ticketName.substring(0, maxLength);
            }

            boolean ticketExists = checkTicketExists(ticketName, channelCategory);

            if (ticketExists) {
                return Optional.of(TicketCreateResult.ALREADY_EXISTS);
            }

            TextChannel ticketChannel = channelCategory.createTextChannel(ticketName + "").complete();

            this.channelId = Optional.of(ticketChannel.getId());
            this.channel = Optional.of(ticketChannel);

            Optional<Ticket> newTicket = createTicket().join();
            TicketMember author = new TicketMember(this, ticketAuthor, DiscordBot.getInstance().getJda().getSelfUser());
            addTicketMember(author).join();

            updateMembers();
            updatePermissions();

            if (newTicket.isEmpty()) {
                return Optional.of(TicketCreateResult.ERROR);
            }

            return Optional.of(TicketCreateResult.SUCCESS);
        });
    }

    /**
     * Update the members of the ticket
     */
    public void updateMembers() {
        if (guild.isEmpty()) {
            return;
        }

        DiscordGuild discordGuild = DiscordGuilds.getGuild(guild.get());

        for (User user : discordGuild.getAllUsers()) {
            TicketMember member = new TicketMember(this, user, DiscordBot.getInstance().getJda().getSelfUser());
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
        return members.stream().anyMatch(member -> member.getMemberId().equals(user.getId()));
    }

    @SuppressWarnings({ "java:S3776", "java:S135" })
    public void updatePermissions() {
        if (channel.isEmpty() || guild.isEmpty()) {
            return;
        }

        Guild guildItem = guild.get();
        DiscordGuild discordGuild = DiscordGuilds.getGuild(guildItem);

        if (discordGuild == null) {
            return;
        }

        TextChannel textChannel = channel.get();
        TextChannelManager manager = textChannel.getManager();

        User botUser = DiscordBot.getInstance().getJda().getSelfUser();
        manager.putMemberPermissionOverride(botUser.getIdLong(), Permission.ALL_PERMISSIONS, 0);

        for (Role role : guildItem.getRoles()) {
            try {
                manager.putRolePermissionOverride(role.getIdLong(), 0, Permission.ALL_PERMISSIONS);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        for (TicketMember member : members) {
            User user = member.getMember().orElse(null);

            if (user == null) {
                continue;
            }

            if (member.isRemoved()) {
                try {
                    manager.putMemberPermissionOverride(Long.valueOf(member.getMemberId()), 0,
                            Permission.ALL_PERMISSIONS);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                continue;
            }

            Collection<Permission> permissions = new ArrayList<>();
            List<GuildRole> roles = discordGuild.getGuildRoles(user.getId());

            boolean adminAdded = false;
            boolean moderatorAdded = false;

            if (roles.isEmpty()) {
                permissions.addAll(getMemberPermissions());
            }

            for (GuildRole role : roles) {
                switch (role) {
                    case DISCORD_ADMINISTRATOR:
                    case SERVER_ADMINISTRATOR:
                        if (!adminAdded) {
                            permissions.addAll(getAdminPermissions());
                            adminAdded = true;
                        }
                        break;

                    case DISCORD_MODERATOR:
                    case SERVER_MODERATOR:
                        if (!moderatorAdded) {
                            permissions.addAll(getModeratorPermissions());
                            moderatorAdded = true;
                        }
                        break;
                }
            }

            manager.putMemberPermissionOverride(user.getIdLong(), permissions, new ArrayList<>());
        }

        manager.complete();
    }

    /**
     * Returns the permissions for the ticket for the channel admin
     *
     * @return The permissions for the ticket
     */
    private Collection<Permission> getAdminPermissions() {
        Collection<Permission> adminPermissions = new ArrayList<>();

        for (Permission permission : Permission.values()) {
            if (permission.isChannel()) {
                adminPermissions.add(permission);
            }
        }

        return adminPermissions;
    }

    /**
     * Returns the permissions for the ticket for the channel moderator
     *
     * @return The permissions for the ticket
     */
    private Collection<Permission> getModeratorPermissions() {
        Collection<Permission> modPermissions = new ArrayList<>();

        for (Permission permission : Permission.values()) {
            if (permission.isChannel()) {
                modPermissions.add(permission);
            }
        }

        return modPermissions;
    }

    /**
     * Returns the permissions for the ticket for the channel member
     *
     * @return The permissions for the ticket
     */
    private Collection<Permission> getMemberPermissions() {
        Collection<Permission> permissions = new ArrayList<>();

        permissions.add(Permission.VIEW_CHANNEL);
        permissions.add(Permission.MESSAGE_ADD_REACTION);
        permissions.add(Permission.MESSAGE_SEND);
        permissions.add(Permission.MESSAGE_EMBED_LINKS);
        permissions.add(Permission.MESSAGE_HISTORY);
        permissions.add(Permission.MESSAGE_EXT_EMOJI);
        permissions.add(Permission.MESSAGE_EXT_STICKER);
        permissions.add(Permission.MESSAGE_ATTACH_FILES);
        permissions.add(Permission.USE_APPLICATION_COMMANDS);

        return permissions;
    }

    /**
     * Check if the ticket exists
     *
     * @param newTicketName   The name of the ticket
     * @param channelCategory The category the ticket should be created in
     * @return If the ticket exists
     */
    private boolean checkTicketExists(String newTicketName, Category channelCategory) {
        boolean channelExists = channelCategory.getChannels().stream()
                .anyMatch(categoryChannel -> categoryChannel.getName().equalsIgnoreCase(newTicketName));

        boolean ticketExists = DiscordBot.getInstance().getTicketManager().getTickets().stream()
                .anyMatch(ticket -> ticket.getChannel().isPresent()
                        && ticket.getChannel().get().getName().equalsIgnoreCase(newTicketName));

        return channelExists || ticketExists;
    }

    /**
     * Close the ticket
     *
     * @return The result of the ticket closing
     */
    public SurfFutureResult<Optional<Ticket>> closeTicket() {
        Optional<String> ticketIdOptional = getTicketId();
        if (ticketIdOptional.isEmpty()) {
            return DataApi.getDataInstance().supplyAsync(Optional::empty);
        }

        String checkedTicketId = ticketIdOptional.get();

        return DataApi.getDataInstance().supplyAsync(() -> {
            String url = String.format(API.TICKET, checkedTicketId);

            WebRequest request = WebRequest.builder().url(url).json(true).parameters(toParameters()).build();
            WebResponse response = request.executeDelete().join();

            if (response.getStatusCode() != 200) {
                return Optional.empty();
            }

            Object body = response.getBody();
            String bodyString = body.toString();

            GsonConverter gson = new GsonConverter();
            JsonObject bodyElement = gson.fromJson(bodyString, JsonObject.class);

            if (!bodyElement.has("data") || !bodyElement.get("data").isJsonObject()) {
                return Optional.empty();
            }

            JsonObject jsonObject = (JsonObject) bodyElement.get("data");
            Ticket newTicket = ticketByJson(jsonObject);

            closedAt = newTicket.closedAt;

            return Optional.of(this);
        });
    }

    /**
     * Close the ticket channel
     *
     * @param user   The user that closed the ticket
     * @param reason The reason the ticket was closed
     * @return The result of the ticket closing
     */
    public SurfFutureResult<TicketCloseResult> closeTicketChannel(User user, String reason) {
        CompletableFuture<TicketCloseResult> future = new CompletableFuture<>();
        DiscordFutureResult<TicketCloseResult> futureResult = new DiscordFutureResult<>(future);

        CompletableFuture.runAsync(() -> {
            if (channel.isEmpty()) {
                future.complete(TicketCloseResult.TICKET_NOT_FOUND);
                return;
            }

            this.closedById = Optional.of(user.getId());
            this.closedBy = Optional.of(user);

            this.closedReason = Optional.of(reason);

            TextChannel textChannel = channel.get();

            closeTicket().whenComplete(newTicketOptional -> {
                if (newTicketOptional.isEmpty()) {
                    future.complete(TicketCloseResult.ERROR);
                    return;
                }

                textChannel.delete().queue(v -> {
                    future.complete(TicketCloseResult.SUCCESS);
                    DiscordBot.getInstance().getTicketManager().removeTicket(this);
                }, error -> {
                    future.complete(TicketCloseResult.ERROR);

                    Launcher.getLogger().logError("Error while closing ticket: " + error.getMessage());
                    error.printStackTrace();
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
     * Get the user that closed the ticket
     *
     * @return The user that closed the ticket
     */
    public Optional<User> getClosedBy() {
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

}
