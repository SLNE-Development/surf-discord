package dev.slne.discord.discord.guild;

import dev.slne.data.api.DataApi;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.discord.guild.reactionrole.ReactionRoleConfig;
import dev.slne.discord.discord.guild.role.DiscordRole;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DiscordGuild {

    private final @Nonnull String guildId;
    private final @Nonnull String categoryId;

    private final @Nonnull String whitelistedRoleId;
    private final Role whitelistedRole;

    private final List<String> discordSupportAdmins;
    private final List<String> serverSupportAdmins;
    private final List<String> discordSupportModerators;
    private final List<String> serverSupportModerators;

    private ReactionRoleConfig reactionRoleConfig;

    /**
     * Construct a new DiscordGuild.
     *
     * @param guildId                  The guild id.
     * @param categoryId               The category id .
     * @param discordSupportAdmins     The discord support admins.
     * @param serverSupportAdmins      The server support admins.
     * @param discordSupportModerators The discord support moderators.
     * @param serverSupportModerators  The server support moderators.
     * @param whitelistedRoleId        The whitelisted role id.
     * @param rrConfig                 The reaction role config.
     */
    @SuppressWarnings("java:S107")
    public DiscordGuild(@Nonnull String guildId, @Nonnull String categoryId, List<String> discordSupportAdmins,
                        List<String> serverSupportAdmins, List<String> discordSupportModerators,
                        List<String> serverSupportModerators, @Nonnull String whitelistedRoleId,
                        ReactionRoleConfig rrConfig) {
        this.guildId = guildId;
        this.categoryId = categoryId;

        this.discordSupportAdmins = discordSupportAdmins;
        this.serverSupportAdmins = serverSupportAdmins;
        this.discordSupportModerators = discordSupportModerators;
        this.serverSupportModerators = serverSupportModerators;

        this.whitelistedRoleId = whitelistedRoleId;
        this.whitelistedRole = DiscordBot.getInstance().getJda().getRoleById(whitelistedRoleId);

        this.reactionRoleConfig = rrConfig;
    }

    /**
     * Returns all users of the guild.
     *
     * @return The users.
     */
    public CompletableFuture<List<User>> getAllUsers() {
        CompletableFuture<List<User>> future = new CompletableFuture<>();

        List<String> userIds = new ArrayList<>();
        List<User> users = new ArrayList<>();

        userIds.addAll(discordSupportAdmins);
        userIds.addAll(serverSupportAdmins);
        userIds.addAll(discordSupportModerators);
        userIds.addAll(serverSupportModerators);

        Guild guild = DiscordBot.getInstance().getJda().getGuildById(guildId);

        if (guild == null) {
            future.complete(users);
            return future;
        }

        List<CompletableFuture<Member>> memberFutures = new ArrayList<>();

        userIds.forEach(userId -> {
            if (userId == null) {
                return;
            }

            memberFutures.add(guild.retrieveMemberById(userId).submit());
        });

        for (CompletableFuture<?> memberFuture : memberFutures) {
            memberFuture.exceptionally(throwable -> {
                DataApi.getDataInstance().logError(getClass(), "Failed to retrieve member", throwable);
                return null;
            });
        }

        CompletableFuture.allOf(memberFutures.toArray(CompletableFuture[]::new)).thenAccept(v -> {
            List<Member> members = memberFutures.stream().map(CompletableFuture::join).toList();

            for (Member member : members) {
                addIfNotExists(users, member.getUser());
            }

            future.complete(users);
        }).exceptionally(throwable -> {
            DataApi.getDataInstance().logError(getClass(), "Failed to retrieve members", throwable);
            return null;
        });

        return future;
    }

    /**
     * Adds a user to the user list.
     *
     * @param userList The user list.
     * @param user     The user.
     */
    private void addIfNotExists(List<User> userList, User user) {
        boolean userListContainsId = userList.stream().anyMatch(userItem -> userItem.getId().equals(user.getId()));

        if (!userListContainsId) {
            userList.add(user);
        }
    }

    /**
     * Returns the guild roles of a user.
     *
     * @param userId The user id.
     *
     * @return The guild roles.
     */
    public List<DiscordRole> getGuildRoles(String userId) {
        List<DiscordRole> roles = new ArrayList<>();

        DiscordRole discordAdminRole = DiscordBot.getInstance().getRoleManager()
                .getRoleByName(DiscordRole.DISCORD_ADMIN_ROLE);
        DiscordRole discordModRole = DiscordBot.getInstance().getRoleManager()
                .getRoleByName(DiscordRole.DISCORD_MOD_ROLE);
        DiscordRole serverAdminRole = DiscordBot.getInstance().getRoleManager()
                .getRoleByName(DiscordRole.SERVER_ADMIN_ROLE);
        DiscordRole serverModRole = DiscordBot.getInstance().getRoleManager()
                .getRoleByName(DiscordRole.SERVER_MOD_ROLE);

        DiscordRole defaultRole = DiscordBot.getInstance().getRoleManager()
                .getRoleByName(DiscordRole.DEFAULT_ROLE);

        if (discordSupportAdmins.contains(userId) && discordAdminRole != null) {
            roles.add(discordAdminRole);
        }

        if (discordSupportModerators.contains(userId) && discordModRole != null) {
            roles.add(discordModRole);
        }

        if (serverSupportAdmins.contains(userId) && serverAdminRole != null) {
            roles.add(serverAdminRole);
        }

        if (serverSupportModerators.contains(userId) && serverModRole != null) {
            roles.add(serverModRole);
        }

        if (roles.isEmpty() && defaultRole != null) {
            roles.add(defaultRole);
        }

        return roles;
    }

    /**
     * Returns the guild id.
     *
     * @return The guild id.
     */
    public @NotNull String getGuildId() {
        return guildId;
    }

    /**
     * Returns the category id.
     *
     * @return The category id.
     */
    public @NotNull String getCategoryId() {
        return categoryId;
    }

    /**
     * @return the discordSupportAdmins
     */
    public List<String> getDiscordSupportAdmins() {
        return discordSupportAdmins;
    }

    /**
     * @return the discordSupportModerators
     */
    public List<String> getDiscordSupportModerators() {
        return discordSupportModerators;
    }

    /**
     * @return the serverSupportAdmins
     */
    public List<String> getServerSupportAdmins() {
        return serverSupportAdmins;
    }

    /**
     * @return the serverSupportModerators
     */
    public List<String> getServerSupportModerators() {
        return serverSupportModerators;
    }

    /**
     * @return the whitelistedRole
     */
    public Role getWhitelistedRole() {
        return whitelistedRole;
    }

    /**
     * @return the whitelistedRoleId
     */
    public @Nonnull String getWhitelistedRoleId() {
        return whitelistedRoleId;
    }

    /**
     * @return the rrConfig
     */
    public ReactionRoleConfig getReactionRoleConfig() {
        return reactionRoleConfig;
    }

    /**
     * @param reactionRoleConfig the rrConfig to set
     */
    public void setReactionRoleConfig(ReactionRoleConfig reactionRoleConfig) {
        this.reactionRoleConfig = reactionRoleConfig;
    }

}
