package dev.slne.discord.discord.guild;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import dev.slne.discord.DiscordBot;
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

    /**
     * Construct a new DiscordGuild.
     *
     * @param guildId                  The guild id.
     * @param categoryId               The category id .
     * @param discordSupportAdmins     The discord support admins.
     * @param serverSupportAdmins      The server support admins.
     * @param discordSupportModerators The discord support moderators.
     * @param serverSupportModerators  The server support moderators.
     */
    public DiscordGuild(@Nonnull String guildId, @Nonnull String categoryId, List<String> discordSupportAdmins,
            List<String> serverSupportAdmins, List<String> discordSupportModerators,
            List<String> serverSupportModerators, @Nonnull String whitelistedRoleId) {
        this.guildId = guildId;
        this.categoryId = categoryId;

        this.discordSupportAdmins = discordSupportAdmins;
        this.serverSupportAdmins = serverSupportAdmins;
        this.discordSupportModerators = discordSupportModerators;
        this.serverSupportModerators = serverSupportModerators;

        this.whitelistedRoleId = whitelistedRoleId;
        this.whitelistedRole = DiscordBot.getInstance().getJda().getRoleById(whitelistedRoleId);
    }

    /**
     * Returns all users of the guild.
     *
     * @return The users.
     */
    public List<User> getAllUsers() {
        List<String> userIds = new ArrayList<>();
        List<User> users = new ArrayList<>();

        userIds.addAll(discordSupportAdmins);
        userIds.addAll(serverSupportAdmins);
        userIds.addAll(discordSupportModerators);
        userIds.addAll(serverSupportModerators);

        Guild guild = DiscordBot.getInstance().getJda().getGuildById(guildId);

        if (guild == null) {
            return users;
        }

        userIds
                .forEach(userId -> {
                    if (userId == null) {
                        return;
                    }

                    Member member = guild.retrieveMemberById(userId).complete();

                    if (member != null) {
                        addIfNotExists(users, member.getUser());
                    }
                });

        return users;
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
    public List<GuildRole> getGuildRoles(String userId) {
        List<GuildRole> roles = new ArrayList<>();

        if (discordSupportAdmins.contains(userId)) {
            roles.add(GuildRole.DISCORD_ADMINISTRATOR);
        }

        if (discordSupportModerators.contains(userId)) {
            roles.add(GuildRole.DISCORD_MODERATOR);
        }

        if (serverSupportAdmins.contains(userId)) {
            roles.add(GuildRole.SERVER_ADMINISTRATOR);
        }

        if (serverSupportModerators.contains(userId)) {
            roles.add(GuildRole.SERVER_MODERATOR);
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

}
