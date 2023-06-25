package dev.slne.discord.ticket;

import java.util.ArrayList;
import java.util.Collection;

import net.dv8tion.jda.api.Permission;

public class TicketPermissions {

    /**
     * Private constructor to prevent instantiation
     */
    private TicketPermissions() {
    }

    /**
     * Returns the permissions for the ticket for the channel admin
     *
     * @return The permissions for the ticket
     */
    public static Collection<Permission> getAdminPermissions() {
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
    public static Collection<Permission> getModeratorPermissions() {
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
    public static Collection<Permission> getMemberPermissions() {
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
}
