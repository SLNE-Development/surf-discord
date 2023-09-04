package dev.slne.discord.ticket;

import dev.slne.data.api.DataApi;
import dev.slne.discord.DiscordBot;
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
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
     *
     * @return when completed
     */
    public static CompletableFuture<Void> addTicketMember(Ticket ticket, TicketMember ticketMember) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Guild guild = ticket.getGuild();
        TextChannel channel = ticket.getChannel();
        RestAction<User> userRest = ticketMember.getMember();

        if (guild == null || channel == null || userRest == null) {
            future.complete(null);
            return future;
        }

        DiscordGuild discordGuild = DiscordGuilds.getGuild(guild);
        TextChannelManager manager = channel.getManager();

        if (discordGuild == null) {
            future.complete(null);
            return future;
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

        return future;
    }

    /**
     * Removes a ticket member from the channel
     *
     * @param ticket       the ticket
     * @param ticketMember the ticket member
     *
     * @return when completed
     */
    public static CompletableFuture<Void> removeTicketMember(Ticket ticket, TicketMember ticketMember) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        TextChannel channel = ticket.getChannel();
        RestAction<User> userRest = ticketMember.getMember();

        if (channel == null || userRest == null) {
            future.complete(null);
            return future;
        }

        TextChannelManager manager = channel.getManager();

        userRest.queue(user -> {
            if (user == null) {
                future.completeExceptionally(new RuntimeException("User not found"));
                return;
            }

            manager.removePermissionOverride(user.getIdLong()).queue(future::complete, future::completeExceptionally);
        }, future::completeExceptionally);

        return future;
    }

    /**
     * Updates the permissions for the ticket channel
     *
     * @param ticket The ticket to update the permissions for
     *
     * @return The future result
     */
    public static CompletableFuture<List<TicketPermissionOverride>> getChannelPermissions(Ticket ticket) {
        CompletableFuture<List<TicketPermissionOverride>> future = new CompletableFuture<>();

        Guild guild = ticket.getGuild();
        List<TicketMember> members = ticket.getMembers();
        List<TicketMember> removedMembers = ticket.getRemovedMembers();

        List<TicketPermissionOverride> overrides = new ArrayList<>();

        if (guild == null) {
            future.complete(overrides);
            return future;
        }

        DiscordGuild discordGuild = DiscordGuilds.getGuild(guild);

        if (discordGuild == null) {
            future.complete(overrides);
            return future;
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
        Role botRole = guild.getBotRole();

        for (Role role : guild.getRoles()) {
            if (role == null) {
                continue;
            }

            if (role.equals(botRole)) {
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

            overrides.add(new TicketPermissionOverride(Type.USER, Long.parseLong(addedUserId),
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
                    Long.parseLong(removedUserId), new ArrayList<>(), allPermissions));
        }
        // #endregion

        future.complete(overrides);
        return future;
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
     *
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
     * @param ticket          The ticket to create the channel for
     * @param ticketName      The name of the ticket
     * @param channelCategory The category to create the ticket in
     *
     * @return The result of the ticket creation
     */
    public static CompletableFuture<TicketCreateResult> createTicketChannel(Ticket ticket, @Nonnull String ticketName,
                                                                            @Nonnull Category channelCategory) {
        CompletableFuture<TicketCreateResult> future = new CompletableFuture<>();

        Guild guild = ticket.getGuild();

        if (guild == null) {
            future.complete(TicketCreateResult.GUILD_NOT_FOUND);
            return future;
        }

        CompletableFuture.runAsync(() -> initialMembers(ticket).thenAcceptAsync(v -> {
            try {
                ChannelAction<TextChannel> channelAction = channelCategory.createTextChannel(ticketName);

                getChannelPermissions(ticket).thenAcceptAsync(overrides -> {
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
                        ticket.setChannelId(ticketChannel.getId());

                        CompletableFuture
                                .allOf(createWebhook(ticket), TicketRepository.updateTicket(ticket))
                                .thenAccept(v1 -> future.complete(TicketCreateResult.SUCCESS));
                    }, exception -> handleException(exception, future));
                });
            } catch (Exception exception) {
                handleException(exception, future);
            }
        }).exceptionally(throwable -> {
            handleException(throwable, future);
            return null;
        }));

        return future;
    }

    /**
     * Handles the exception
     *
     * @param throwable The exception
     * @param future    The future
     */
    @SuppressWarnings("java:S1871")
    private static void handleException(Throwable throwable, CompletableFuture<TicketCreateResult> future) {
        if (throwable instanceof ErrorResponseException errorResponseException
                && errorResponseException.getErrorCode() == 50013) {
            future.complete(TicketCreateResult.MISSING_PERMISSIONS);
            return;
        } else if (throwable instanceof InsufficientPermissionException) {
            future.complete(TicketCreateResult.MISSING_PERMISSIONS);
            return;
        }

        future.complete(TicketCreateResult.ERROR);
        DataApi.getDataInstance().logError(TicketChannel.class, "Failed to create ticket channel.", throwable);
    }

    /**
     * Get the name for the ticket channel
     *
     * @param ticket The ticket to get the name for
     *
     * @return The name for the ticket channel
     */
    public static CompletableFuture<String> getTicketName(Ticket ticket) {
        CompletableFuture<String> future = new CompletableFuture<>();

        TicketType ticketType = ticket.getTicketType();
        ticket.getTicketAuthor().queue(ticketAuthor -> {
            if (ticketType == null || ticketAuthor == null) {
                future.complete(null);
                return;
            }

            String ticketTypeName = ticketType.name().toLowerCase();
            String authorName = ticketAuthor.getName().toLowerCase().trim().replace(" ", "-");

            int maxLength = Channel.MAX_NAME_LENGTH;
            String ticketName = ticketTypeName + "-" + authorName;

            if (ticketName.length() > maxLength) {
                ticketName = ticketName.substring(0, maxLength);
            }

            future.complete(ticketName);
        });

        return future;
    }

    /**
     * Creates the webhook for the ticket channel
     *
     * @param ticket The ticket to create the webhook for
     */
    public static CompletableFuture<Void> createWebhook(Ticket ticket) {
        return CompletableFuture.supplyAsync(() -> {
            TextChannel channel = ticket.getChannel();

            if (channel == null) {
                return null;
            }

            channel.createWebhook("Ticket Webhook").queue(webhook -> {
                ticket.setWebhookId(webhook.getId());
                ticket.setWebhookName(webhook.getName());
                ticket.setWebhookUrl(webhook.getUrl());
            });

            return null;
        });
    }

    /**
     * Deletes the ticket channel
     *
     * @param ticket The ticket to delete the channel for
     *
     * @return The future result
     */
    public static CompletableFuture<Void> deleteTicketChannel(Ticket ticket) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        TextChannel channel = ticket.getChannel();

        if (channel == null) {
            future.complete(null);
            return future;
        }

        channel.delete().queue(future::complete, future::completeExceptionally);

        return future;
    }

    /**
     * Check if the ticket exists
     *
     * @param newTicketName   The name of the ticket
     * @param channelCategory The category the ticket should be created in
     *
     * @return If the ticket exists
     */
    public static CompletableFuture<Boolean> checkTicketExists(String newTicketName, Category channelCategory,
                                                               TicketType newTicketType, User newAuthor) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        boolean channelExists = channelCategory.getChannels().stream()
                .anyMatch(categoryChannel -> categoryChannel.getName().equalsIgnoreCase(newTicketName));

        if (channelExists) {
            future.complete(true);
            return future;
        }

        boolean hasTicket = false;
        for (Ticket ticket : DiscordBot.getInstance().getTicketManager().getTickets()) {
            if (newAuthor.getId().equalsIgnoreCase(ticket.getTicketAuthorId())
                    && newTicketType == ticket.getTicketType()) {
                hasTicket = true;
                break;
            }
        }

        future.complete(hasTicket);
        return future;
    }

    /**
     * Update the members of the ticket
     */
    @SuppressWarnings("java:S3776")
    public static CompletableFuture<Void> initialMembers(Ticket ticket) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Guild guild = ticket.getGuild();
        RestAction<User> ticketAuthor = ticket.getTicketAuthor();
        TicketType ticketType = ticket.getTicketType();

        if (guild == null) {
            future.complete(null);
            return future;
        }

        List<CompletableFuture<TicketMember>> futures = new ArrayList<>();
        User artyUser = DiscordBot.getInstance().getJda().getSelfUser();
        futures.add(ticket.addTicketMember(new TicketMember(ticket, artyUser, artyUser)));

        ticketAuthor.queue(author -> {
            DiscordGuild discordGuild = DiscordGuilds.getGuild(guild);
            discordGuild.getAllUsers().thenAcceptAsync(allUsers -> {
                if (!allUsers.contains(author)) {
                    futures.add(ticket.addTicketMember(
                            new TicketMember(ticket, author, DiscordBot.getInstance().getJda().getSelfUser())));
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
                        futures.add(ticket.addTicketMember(new TicketMember(ticket, user, artyUser)));
                    }
                }

                CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                        .thenAcceptAsync(v -> future.complete(null));
            }).exceptionally(throwable -> {
                future.completeExceptionally(throwable);
                return null;
            });
        }, future::completeExceptionally);

        return future;
    }
}
