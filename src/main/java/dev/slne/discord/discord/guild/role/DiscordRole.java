package dev.slne.discord.discord.guild.role;

import com.google.common.base.MoreObjects;
import dev.slne.discord.discord.guild.permission.DiscordPermission;
import dev.slne.discord.ticket.TicketType;
import net.dv8tion.jda.api.Permission;

import java.util.List;

public class DiscordRole {

    public static final String SERVER_ADMIN_ROLE = "ServerSupportAdmin";
    public static final String SERVER_MOD_ROLE = "ServerSupportModerator";
    public static final String DISCORD_ADMIN_ROLE = "DiscordSupportAdmin";
    public static final String DISCORD_MOD_ROLE = "DiscordSupportModerator";
    public static final String DEFAULT_ROLE = "Default";

    private final String name;
    private final List<DiscordPermission> permissions;

    /**
     * Construct a new DiscordRole.
     *
     * @param name        The name of the role.
     * @param permissions The permissions of the role.
     */
    public DiscordRole(String name, List<DiscordPermission> permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    /**
     * Returns the list of permissions that are allowed in discord.
     *
     * @return the list of permissions that are allowed in discord
     */
    public List<Permission> getDiscordAllowedPermissions() {
        return permissions.stream().filter(DiscordPermission::isDiscordPermission).map(DiscordPermission::getPermission)
                .toList();
    }

    /**
     * Returns true if the role can view the given ticket channel.
     *
     * @param ticketType the ticket type
     *
     * @return true if the role can view the given ticket channel
     */
    public boolean canViewTicketChannel(TicketType ticketType) {
        boolean canView = false;

        switch (ticketType) {
            case BUGREPORT -> canView = hasRolePermission(DiscordPermission.VIEW_BUGREPORT_TICKETS);
            case DISCORD_SUPPORT -> canView = hasRolePermission(DiscordPermission.VIEW_DISCORD_SUPPORT_TICKETS);
            case SERVER_SUPPORT -> canView = hasRolePermission(DiscordPermission.VIEW_SERVER_SUPPORT_TICKETS);
            case WHITELIST -> canView = hasRolePermission(DiscordPermission.VIEW_WHITELIST_TICKETS);
            default -> {
            }
        }

        if (hasRolePermission(DiscordPermission.VIEW_ALL_TICKETS)) {
            canView = true;
        }

        return canView;
    }

    /**
     * Returns true if the role has the given permission.
     *
     * @param permission the permission to check
     *
     * @return true if the role has the given permission
     */
    public boolean hasDiscordPermission(Permission permission) {
        if (permissions.contains(DiscordPermission.ALL) || permissions.contains(DiscordPermission.ALL_DISCORD)) {
            return true;
        }

        boolean hasPermission = false;

        for (DiscordPermission discordPermission : permissions) {
            if (discordPermission.getPermission() != null
                    && discordPermission.getPermission().name().equalsIgnoreCase(permission.name())) {
                hasPermission = true;
                break;
            }
        }

        return hasPermission;
    }

    /**
     * Returns true if the role has the given permission.
     *
     * @param permission the permission to check
     *
     * @return true if the role has the given permission
     */
    public boolean hasRolePermission(DiscordPermission permission) {
        return permissions.contains(permission) || permissions.contains(DiscordPermission.ALL_ROLE)
                || permissions.contains(DiscordPermission.ALL);
    }

    /**
     * Returns the permissions of the role.
     *
     * @return the permissions
     */
    public List<DiscordPermission> getPermissions() {
        return permissions;
    }

    /**
     * Returns the name of the role.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("permissions", permissions)
                .toString();
    }
}
