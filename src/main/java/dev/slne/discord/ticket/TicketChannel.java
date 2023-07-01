package dev.slne.discord.ticket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

import dev.slne.data.core.database.future.SurfFutureResult;
import dev.slne.data.core.instance.DataApi;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.datasource.database.future.DiscordFutureResult;
import dev.slne.discord.discord.guild.DiscordGuild;
import dev.slne.discord.discord.guild.DiscordGuilds;
import dev.slne.discord.discord.guild.role.DiscordRole;
import dev.slne.discord.ticket.TicketPermissionOverride.Type;
import dev.slne.discord.ticket.member.TicketMember;
import dev.slne.discord.ticket.result.TicketCreateResult;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

public class TicketChannel {

    /**
     * Private constructor to prevent instantiation
     */
    private TicketChannel() {
    }

    /**
     * Adds a ticket member to the channel
     *
     * @param ticket       the ticket
     * @param ticketMember the ticket member
     * @return when completed
     */
    public static SurfFutureResult<Void> addTicketMember(Ticket ticket, TicketMember ticketMember) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        DiscordFutureResult<Void> futureResult = new DiscordFutureResult<>(future);

        Guild guild = ticket.getGuild().orElse(null);
        TextChannel channel = ticket.getChannel().orElse(null);
        RestAction<User> userRest = ticketMember.getMember().orElse(null);

        if (guild == null || channel == null || userRest == null) {
            future.complete(null);
            return futureResult;
        }

        DiscordGuild discordGuild = DiscordGuilds.getGuild(guild);
        TextChannelManager manager = channel.getManager();

        if (discordGuild == null) {
            future.complete(null);
            return futureResult;
        }

        userRest.queue(user -> {
            if (user == null) {
                future.completeExceptionally(new RuntimeException("User not found"));
                return;
            }

            List<DiscordRole> roles = discordGuild.getGuildRoles(user.getId());
            List<Permission> permissions = new ArrayList<>();

            for (DiscordRole role : roles) {
                for (Permission permission : role.getDiscordAllowedPermissions()) {
                    if (!permissionAdded(permissions, permission)) {
                        permissions.add(permission);
                    }
                }
            }

            manager.putMemberPermissionOverride(user.getIdLong(), permissions, new ArrayList<>())
                    .queue(future::complete, future::completeExceptionally);
        }, future::completeExceptionally);

        return futureResult;
    }

    /**
     * Removes a ticket member from the channel
     *
     * @param ticket       the ticket
     * @param ticketMember the ticket member
     * @return when completed
     */
    public static SurfFutureResult<Void> removeTicketMember(Ticket ticket, TicketMember ticketMember) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        DiscordFutureResult<Void> futureResult = new DiscordFutureResult<>(future);

        TextChannel channel = ticket.getChannel().orElse(null);
        RestAction<User> userRest = ticketMember.getMember().orElse(null);

        if (channel == null || userRest == null) {
            future.complete(null);
            return futureResult;
        }

        TextChannelManager manager = channel.getManager();

        userRest.queue(user -> {
            if (user == null) {
                future.completeExceptionally(new RuntimeException("User not found"));
                return;
            }

            manager.removePermissionOverride(user.getIdLong()).queue(future::complete, future::completeExceptionally);
        }, future::completeExceptionally);

        return futureResult;
    }

    /**
     * Updates the permissions for the ticket channel
     */
    @SuppressWarnings({ "java:S3776", "java:S135", "java:S1192" })
    public static SurfFutureResult<List<TicketPermissionOverride>> getChannelPermissions(Ticket ticket,
            Category channelCategory) {
        CompletableFuture<List<TicketPermissionOverride>> future = new CompletableFuture<>();
        DiscordFutureResult<List<TicketPermissionOverride>> futureResult = new DiscordFutureResult<>(future);

        Optional<Guild> guild = ticket.getGuild();
        List<TicketMember> members = ticket.getMembers();
        List<TicketMember> removedMembers = ticket.getRemovedMembers();

        List<TicketPermissionOverride> overrides = new ArrayList<>();

        if (guild.isEmpty()) {
            future.complete(overrides);
            return futureResult;
        }

        Guild guildItem = guild.get();
        DiscordGuild discordGuild = DiscordGuilds.getGuild(guildItem);

        if (discordGuild == null) {
            future.complete(overrides);
            return futureResult;
        }

        List<String> removedUserIds = new ArrayList<>();
        List<String> addedUserIds = new ArrayList<>();

        List<Permission> allPermissions = getAllPermissions();

        // #region Members
        for (TicketMember member : members) {
            if (member.isRemoved()) {
                continue;
            }

            addedUserIds.add(member.getMemberId());
        }
        // #endregion

        // #region Removed Members
        for (TicketMember removedMember : new ArrayList<>(removedMembers)) {
            removedUserIds.add(removedMember.getMemberId());
        }
        // #endregion

        // #region Roles
        Role botRole = guildItem.getBotRole();

        for (Role role : guildItem.getRoles()) {
            if (role == null) {
                continue;
            }

            if (botRole != null && role.equals(botRole)) {
                overrides.add(
                        new TicketPermissionOverride(Type.ROLE, role.getIdLong(), allPermissions, new ArrayList<>()));
            } else {
                overrides.add(new TicketPermissionOverride(Type.ROLE, role.getIdLong(), new ArrayList<>(),
                        allPermissions));
            }
        }
        // #endregion

        // #region Bot
        User botUser = DiscordBot.getInstance().getJda().getSelfUser();
        overrides.add(new TicketPermissionOverride(Type.USER, botUser.getIdLong(), allPermissions,
                new ArrayList<>()));
        // #endregion

        // #region Added users
        for (String addedUserId : addedUserIds) {
            if (addedUserId == null) {
                continue;
            }

            if (addedUserId.equals(botUser.getId())) {
                continue;
            }

            Collection<Permission> permissions = new ArrayList<>();
            List<DiscordRole> roles = discordGuild.getGuildRoles(addedUserId);

            for (DiscordRole role : roles) {
                for (Permission permission : role.getDiscordAllowedPermissions()) {
                    if (!permissionAdded(permissions, permission)) {
                        permissions.add(permission);
                    }
                }
            }

            overrides.add(new TicketPermissionOverride(Type.USER, Long.valueOf(addedUserId),
                    permissions, new ArrayList<>()));
        }
        // #endregion

        // #region Removed users
        for (String removedUserId : removedUserIds) {
            if (removedUserId == null) {
                continue;
            }

            if (removedUserId.equals(botUser.getId())) {
                continue;
            }

            overrides.add(new TicketPermissionOverride(Type.USER,
                    Long.valueOf(removedUserId), new ArrayList<>(), allPermissions));
        }
        // #endregion

        future.complete(overrides);
        return futureResult;
    }

    /**
     * Returns all permissions
     *
     * @return all permissions
     */
    private static List<Permission> getAllPermissions() {
        List<Permission> allPermissions = new ArrayList<>();

        for (Permission perm : Permission.values()) {
            if (perm.isText()) {
                allPermissions.add(perm);
            }
        }
        allPermissions.add(Permission.VIEW_CHANNEL);
        allPermissions.add(Permission.MANAGE_WEBHOOKS);
        allPermissions.add(Permission.MANAGE_CHANNEL);

        return allPermissions;
    }

    /**
     * Returns if the permissions list contans the given permission
     *
     * @param permissions The permissions to check in
     * @param permission  The permission to check
     * @return If the permissions list contans the given permission
     */
    private static boolean permissionAdded(Collection<Permission> permissions, Permission permission) {
        boolean added = false;

        for (Permission perm : permissions) {
            if (perm == permission) {
                added = true;
                break;
            }
        }

        return added;
    }

    /**
     * Create the ticket channel
     *
     * @param ticket The ticket to create the channel for
     * @return The result of the ticket creation
     */
    @SuppressWarnings({ "java:S3776", "java:S135", "java:S1192" })
    public static SurfFutureResult<Optional<TicketCreateResult>> createTicketChannel(Ticket ticket,
            @Nonnull String ticketName, @Nonnull Category channelCategory) {
        CompletableFuture<Optional<TicketCreateResult>> future = new CompletableFuture<>();
        DiscordFutureResult<Optional<TicketCreateResult>> futureResult = new DiscordFutureResult<>(future);

        Optional<Guild> guildOptional = ticket.getGuild();

        if (guildOptional.isEmpty()) {
            future.complete(Optional.of(TicketCreateResult.GUILD_NOT_FOUND));
            return futureResult;
        }

        DataApi.getDataInstance().runAsync(() -> initialMembers(ticket).whenComplete(v -> {
            ChannelAction<TextChannel> channelAction = channelCategory.createTextChannel(ticketName);

            getChannelPermissions(ticket, channelCategory).whenComplete(overrides -> {
                for (TicketPermissionOverride override : overrides) {
                    if (override.getType() == Type.ROLE) {
                        channelAction.addRolePermissionOverride(override.getId(),
                                override.getAllow(), override.getDeny());
                    } else if (override.getType() == Type.USER) {
                        channelAction.addMemberPermissionOverride(override.getId(),
                                override.getAllow(), override.getDeny());
                    }
                }

                channelAction.queue(ticketChannel -> {
                    ticket.setChannel(Optional.of(ticketChannel));
                    ticket.setChannelId(Optional.of(ticketChannel.getId()));

                    createWebhook(ticket).join();
                    TicketRepository.updateTicket(ticket).join();

                    future.complete(Optional.of(TicketCreateResult.SUCCESS));
                }, exception -> {
                    if (exception instanceof ErrorResponseException errorResponseException
                            && errorResponseException.getErrorCode() == 50013) {
                        future.complete(Optional.of(TicketCreateResult.MISSING_PERMISSIONS));
                        return;
                    }

                    future.complete(Optional.of(TicketCreateResult.ERROR));
                    exception.printStackTrace();
                });
            });
        }));

        return futureResult;
    }

    /**
     * Get the name for the ticket channel
     *
     * @param ticket The ticket to get the name for
     * @return The name for the ticket channel
     */
    public static SurfFutureResult<Optional<String>> getTicketName(Ticket ticket) {
        CompletableFuture<Optional<String>> future = new CompletableFuture<>();
        DiscordFutureResult<Optional<String>> futureResult = new DiscordFutureResult<>(future);

        TicketType ticketType = ticket.getTicketType();
        ticket.getTicketAuthor().queue(ticketAuthor -> {
            if (ticketType == null || ticketAuthor == null) {
                future.complete(Optional.empty());
                return;
            }

            String ticketTypeName = ticketType.name().toLowerCase();
            String authorName = null;

            if (ticketAuthor != null && ticketAuthor.getName() != null) {
                authorName = ticketAuthor.getName().toLowerCase().trim().replace(" ", "-");
            } else {
                authorName = "unknown";
            }

            int maxLength = Channel.MAX_NAME_LENGTH;
            String ticketName = ticketTypeName + "-" + authorName;

            if (ticketName.length() > maxLength) {
                ticketName = ticketName.substring(0, maxLength);
            }

            future.complete(Optional.of(ticketName));
        });

        return futureResult;
    }

    /**
     * Creates the webhook for the ticket channel
     *
     * @param ticket The ticket to create the webhook for
     */
    public static SurfFutureResult<Void> createWebhook(Ticket ticket) {
        return DataApi.getDataInstance().supplyAsync(() -> {
            Optional<TextChannel> channel = ticket.getChannel();

            if (channel.isEmpty()) {
                return null;
            }

            TextChannel textChannel = channel.get();
            textChannel.createWebhook("Ticket Webhook").queue(webhook -> ticket.setWebhook(Optional.of(webhook)));

            return null;
        });
    }

    /**
     * Deletes the ticket channel
     *
     * @param ticket The ticket to delete the channel for
     * @return The future result
     */
    public static SurfFutureResult<Void> deleteTicketChannel(Ticket ticket) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        DiscordFutureResult<Void> futureResult = new DiscordFutureResult<>(future);

        Optional<TextChannel> channel = ticket.getChannel();

        if (channel.isEmpty()) {
            future.complete(null);
            return futureResult;
        }

        TextChannel textChannel = channel.get();

        if (textChannel == null) {
            future.complete(null);
            return futureResult;
        }

        textChannel.delete().queue(future::complete, future::completeExceptionally);

        return futureResult;
    }

    /**
     * Check if the ticket exists
     *
     * @param newTicketName   The name of the ticket
     * @param channelCategory The category the ticket should be created in
     * @return If the ticket exists
     */
    public static boolean checkTicketExists(String newTicketName, Category channelCategory, TicketType newTicketType) {
        boolean channelExists = channelCategory.getChannels().stream()
                .anyMatch(categoryChannel -> categoryChannel.getName().equalsIgnoreCase(newTicketName));

        boolean ticketNameChannelExists = DiscordBot.getInstance().getTicketManager().getTickets().stream()
                .anyMatch(ticket -> ticket.getChannel().isPresent()
                        && ticket.getChannel().get().getName().equalsIgnoreCase(newTicketName));

        boolean ticketTypeMatches = DiscordBot.getInstance().getTicketManager().getTickets().stream()
                .anyMatch(ticket -> ticket.getTicketType() == newTicketType);

        return channelExists || ticketNameChannelExists || ticketTypeMatches;
    }

    /**
     * Update the members of the ticket
     */
    @SuppressWarnings("java:S3776")
    public static SurfFutureResult<Void> initialMembers(Ticket ticket) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        DiscordFutureResult<Void> futureResult = new DiscordFutureResult<>(future);

        Optional<Guild> guild = ticket.getGuild();
        RestAction<User> ticketAuthor = ticket.getTicketAuthor();
        TicketType ticketType = ticket.getTicketType();

        if (guild.isEmpty()) {
            future.complete(null);
            return futureResult;
        }

        List<CompletableFuture<Optional<TicketMember>>> futures = new ArrayList<>();
        User artyUser = DiscordBot.getInstance().getJda().getSelfUser();
        futures.add(ticket.addTicketMember(new TicketMember(ticket, artyUser, artyUser)).getFuture());

        ticketAuthor.queue(author -> {
            DiscordGuild discordGuild = DiscordGuilds.getGuild(guild.get());
            List<User> allUsers = discordGuild.getAllUsers().join();

            if (!allUsers.contains(author)) {
                futures.add(
                        ticket.addTicketMember(
                                new TicketMember(ticket, author, DiscordBot.getInstance().getJda().getSelfUser()))
                                .getFuture());
            }

            for (User user : allUsers) {
                List<DiscordRole> userRoles = discordGuild.getGuildRoles(user.getId());

                boolean canSeeTicket = false;
                for (DiscordRole role : userRoles) {
                    if (role.canViewTicketChannel(ticketType)) {
                        canSeeTicket = true;
                        break;
                    }
                }

                if (user.equals(author)) {
                    canSeeTicket = true;
                }

                if (canSeeTicket) {
                    futures.add(ticket.addTicketMember(new TicketMember(ticket, user, artyUser)).getFuture());
                }
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[futures.size()]))
                    .thenAcceptAsync(v -> future.complete(null));
        });

        return futureResult;
    }
}
