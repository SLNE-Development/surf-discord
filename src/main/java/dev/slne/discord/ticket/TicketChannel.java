package dev.slne.discord.ticket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import dev.slne.data.core.database.future.SurfFutureResult;
import dev.slne.data.core.instance.DataApi;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.datasource.database.future.DiscordFutureResult;
import dev.slne.discord.discord.guild.DiscordGuild;
import dev.slne.discord.discord.guild.DiscordGuilds;
import dev.slne.discord.discord.guild.role.DiscordRole;
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

public class TicketChannel {

    /**
     * Private constructor to prevent instantiation
     */
    private TicketChannel() {
    }

    /**
     * Updates the permissions for the ticket channel
     */
    @SuppressWarnings({ "java:S3776", "java:S135", "java:S6541" })
    public static void updateChannelPermissions(Ticket ticket) {
        Optional<TextChannel> channel = ticket.getChannel();
        Optional<Guild> guild = ticket.getGuild();
        List<TicketMember> members = ticket.getMembers();
        List<TicketMember> removedMembers = ticket.getRemovedMembers();

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

        for (TicketMember removedMember : new ArrayList<>(removedMembers)) {
            if (removedMember.isRemoved()) {
                manager.removePermissionOverride(Long.valueOf(removedMember.getMemberId()));
                removedMembers.remove(removedMember);
            }
        }

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
            if (member.isRemoved()) {
                manager.removePermissionOverride(Long.valueOf(member.getMemberId()));
                continue;
            }

            RestAction<User> userRest = member.getMember().orElse(null);
            if (userRest == null) {
                continue;
            }

            User user = userRest.complete();
            if (user == null) {
                continue;
            }

            if (user.equals(botUser)) {
                continue;
            }

            Collection<Permission> permissions = new ArrayList<>();
            List<DiscordRole> roles = discordGuild.getGuildRoles(user.getId());

            if (roles.isEmpty()) {
                permissions.addAll(TicketPermissions.getMemberPermissions());
            }

            for (DiscordRole role : roles) {
                for (Permission permission : role.getDiscordAllowedPermissions()) {
                    if (!permissionAdded(permissions, permission)) {
                        permissions.add(permission);
                    }
                }
            }

            manager.putMemberPermissionOverride(user.getIdLong(), permissions, new ArrayList<>());
        }

        manager.queue();
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
    public static SurfFutureResult<Optional<TicketCreateResult>> createTicketChannel(Ticket ticket) {
        CompletableFuture<Optional<TicketCreateResult>> future = new CompletableFuture<>();
        DiscordFutureResult<Optional<TicketCreateResult>> futureResult = new DiscordFutureResult<>(future);

        Optional<Guild> guildOptional = ticket.getGuild();

        if (guildOptional.isEmpty()) {
            future.complete(Optional.of(TicketCreateResult.GUILD_NOT_FOUND));
            return futureResult;
        }

        Guild guild = guildOptional.get();

        DataApi.getDataInstance().runAsync(() -> {
            DiscordGuild discordGuild = DiscordGuilds.getGuild(guild);

            if (discordGuild == null) {
                future.complete(Optional.of(TicketCreateResult.GUILD_NOT_FOUND));
                return;
            }

            String categoryId = discordGuild.getCategoryId();
            if (categoryId == null) {
                future.complete(Optional.of(TicketCreateResult.CATEGORY_NOT_FOUND));
                return;
            }

            Category channelCategory = guild.getCategoryById(categoryId);

            if (channelCategory == null) {
                future.complete(Optional.of(TicketCreateResult.CATEGORY_NOT_FOUND));
                return;
            }

            getTicketName(ticket).whenComplete(ticketNameOptional -> {
                if (ticketNameOptional.isEmpty()) {
                    future.complete(Optional.of(TicketCreateResult.ERROR));
                    return;
                }

                String ticketName = ticketNameOptional.get();

                if (ticketName == null) {
                    future.complete(Optional.of(TicketCreateResult.ERROR));
                    return;
                }

                boolean ticketExists = checkTicketExists(ticketName, channelCategory);

                if (ticketExists) {
                    future.complete(Optional.of(TicketCreateResult.ALREADY_EXISTS));
                    return;
                }

                try {
                    channelCategory.createTextChannel(ticketName).queue(ticketChannel -> {
                        ticket.setChannel(Optional.of(ticketChannel));
                        ticket.setChannelId(Optional.of(ticketChannel.getId()));

                        createWebhook(ticket).join();
                        TicketRepository.updateTicket(ticket).join();

                        future.complete(Optional.of(TicketCreateResult.SUCCESS));
                    }, future::completeExceptionally);
                } catch (Exception exception) {
                    if (exception instanceof ErrorResponseException errorResponseException
                            && errorResponseException.getErrorCode() == 50013) {
                        future.complete(Optional.of(TicketCreateResult.MISSING_PERMISSIONS));
                        errorResponseException.printStackTrace();
                        return;
                    }

                    future.complete(Optional.of(TicketCreateResult.ERROR));
                    exception.printStackTrace();
                }
            }, future::completeExceptionally);
        });

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
    public static boolean checkTicketExists(String newTicketName, Category channelCategory) {
        boolean channelExists = channelCategory.getChannels().stream()
                .anyMatch(categoryChannel -> categoryChannel.getName().equalsIgnoreCase(newTicketName));

        boolean ticketExists = DiscordBot.getInstance().getTicketManager().getTickets().stream()
                .anyMatch(ticket -> ticket.getChannel().isPresent()
                        && ticket.getChannel().get().getName().equalsIgnoreCase(newTicketName));

        return channelExists || ticketExists;
    }
}
