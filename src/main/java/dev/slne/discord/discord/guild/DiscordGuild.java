package dev.slne.discord.discord.guild;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

import dev.slne.data.core.database.future.SurfFutureResult;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.datasource.database.future.DiscordFutureResult;
import dev.slne.discord.discord.guild.reactionrole.ReactionRoleConfig;
import dev.slne.discord.discord.guild.role.DiscordRole;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class DiscordGuild {

    private @Nonnull String guildId;
    private @Nonnull String categoryId;

    private @Nonnull String whitelistedRoleId;
    private Role whitelistedRole;

    private List<String> discordSupportAdmins;
    private List<String> serverSupportAdmins;
    private List<String> discordSupportModerators;
    private List<String> serverSupportModerators;

    private ReactionRoleConfig rrConfig;

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
            List<String> serverSupportModerators, @Nonnull String whitelistedRoleId, ReactionRoleConfig rrConfig) {
        this.guildId = guildId;
        this.categoryId = categoryId;

        this.discordSupportAdmins = discordSupportAdmins;
        this.serverSupportAdmins = serverSupportAdmins;
        this.discordSupportModerators = discordSupportModerators;
        this.serverSupportModerators = serverSupportModerators;

        this.whitelistedRoleId = whitelistedRoleId;
        this.whitelistedRole = DiscordBot.getInstance().getJda().getRoleById(whitelistedRoleId);

        this.rrConfig = rrConfig;
    }

    /**
     * Returns all users of the guild.
     *
     * @return The users.
     */
    public SurfFutureResult<List<User>> getAllUsers() {
        CompletableFuture<List<User>> future = new CompletableFuture<>();
        DiscordFutureResult<List<User>> futureResult = new DiscordFutureResult<>(future);

        List<String> userIds = new ArrayList<>();
        List<User> users = new ArrayList<>();

        userIds.addAll(discordSupportAdmins);
        userIds.addAll(serverSupportAdmins);
        userIds.addAll(discordSupportModerators);
        userIds.addAll(serverSupportModerators);

        Guild guild = DiscordBot.getInstance().getJda().getGuildById(guildId);

        if (guild == null) {
            future.complete(users);
            return futureResult;
        }

        List<CompletableFuture<Member>> memberFutures = new ArrayList<>();

        userIds.forEach(userId -> {
            if (userId == null) {
                return;
            }

            memberFutures.add(guild.retrieveMemberById(userId).submit());
        });

        CompletableFuture.allOf(memberFutures.toArray(new CompletableFuture<?>[memberFutures.size()])).thenAccept(v -> {
            List<Member> members = memberFutures.stream().map(CompletableFuture::join).toList();

            for (Member member : members) {
                addIfNotExists(users, member.getUser());
            }

            future.complete(users);
        });

        return futureResult;
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
     * @return The guild roles.
     */
    public List<DiscordRole> getGuildRoles(String userId) {
        List<DiscordRole> roles = new ArrayList<>();

        Optional<DiscordRole> discordAdminRoleOptional = DiscordBot.getInstance().getRoleManager()
                .getRoleByName(DiscordRole.DISCORD_ADMIN_ROLE);
        Optional<DiscordRole> discordModRoleOptional = DiscordBot.getInstance().getRoleManager()
                .getRoleByName(DiscordRole.DISCORD_MOD_ROLE);
        Optional<DiscordRole> serverAdminRoleOptional = DiscordBot.getInstance().getRoleManager()
                .getRoleByName(DiscordRole.SERVER_ADMIN_ROLE);
        Optional<DiscordRole> serverModRoleOptional = DiscordBot.getInstance().getRoleManager()
                .getRoleByName(DiscordRole.SERVER_MOD_ROLE);

        if (discordSupportAdmins.contains(userId) && discordAdminRoleOptional.isPresent()) {
            roles.add(discordAdminRoleOptional.get());
        }

        if (discordSupportModerators.contains(userId) && discordModRoleOptional.isPresent()) {
            roles.add(discordModRoleOptional.get());
        }

        if (serverSupportAdmins.contains(userId) && serverAdminRoleOptional.isPresent()) {
            roles.add(serverAdminRoleOptional.get());
        }

        if (serverSupportModerators.contains(userId) && serverModRoleOptional.isPresent()) {
            roles.add(serverModRoleOptional.get());
        }

        return roles;
    }

    /**
     * Returns the guild id.
     *
     * @return The guild id.
     */
    public String getGuildId() {
        return guildId;
    }

    /**
     * Returns the category id.
     *
     * @return The category id.
     */
    public String getCategoryId() {
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
        return rrConfig;
    }

}
