package dev.slne.discord.discord.guild.role;

import java.util.List;

import dev.slne.discord.discord.guild.permission.DiscordPermission;
import dev.slne.discord.ticket.TicketType;
import net.dv8tion.jda.api.Permission;

public class DiscordRole {

    public static final String SERVER_ADMIN_ROLE = "ServerAdmin";
    public static final String SERVER_MOD_ROLE = "ServerMod";
    public static final String DISCORD_ADMIN_ROLE = "DiscordAdmin";
    public static final String DISCORD_MOD_ROLE = "DiscordMod";
    public static final String DEFAULT_ROLE = "Default";

    private String name;
    private List<DiscordPermission> permissions;

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
     * @return true if the role can view the given ticket channel
     */
    public boolean canViewTicketChannel(TicketType ticketType) {
        boolean canView = false;

        switch (ticketType) {
            case BUGREPORT:
                canView = hasRolePermission(DiscordPermission.VIEW_BUGREPORT_TICKETS);
                break;
            case DISCORD_SUPPORT:
                canView = hasRolePermission(DiscordPermission.VIEW_DISCORD_SUPPORT_TICKETS);
                break;
            case SERVER_SUPPORT:
                canView = hasRolePermission(DiscordPermission.VIEW_SERVER_SUPPORT_TICKETS);
                break;
            case WHITELIST:
                canView = hasRolePermission(DiscordPermission.VIEW_WHITELIST_TICKETS);
                break;

            default:
                break;
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
            }
        }

        return hasPermission;
    }

    /**
     * Returns true if the role has the given permission.
     *
     * @param permission the permission to check
     * @return true if the role has the given permission
     */
    public boolean hasRolePermission(DiscordPermission permission) {
        return permissions.contains(permission) || permissions.contains(DiscordPermission.ALL_ROLE)
                || permissions.contains(DiscordPermission.ALL);
    }

    /**
     * @return the permissions
     */
    public List<DiscordPermission> getPermissions() {
        return permissions;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

}
