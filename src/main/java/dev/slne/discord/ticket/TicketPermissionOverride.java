package dev.slne.discord.ticket;

import com.google.common.base.MoreObjects;
import net.dv8tion.jda.api.Permission;

import java.util.Collection;

/**
 * Create a new permission override for a ticket.
 *
 * @param type  The type of override, either {@link Type#ROLE} or
 *              {@link Type#USER}
 * @param id    The id of the role or user
 * @param allow The permissions to allow
 * @param deny  The permissions to deny
 */
public record TicketPermissionOverride(Type type, long id,
                                       Collection<Permission> allow, Collection<Permission> deny) {

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (other == null) {
            return false;
        }

        if (other instanceof TicketPermissionOverride otherOverride) {
            boolean allowMatches = allow().equals(otherOverride.allow());
            boolean denyMatches = deny().equals(otherOverride.deny());
            boolean idMatches = id() == otherOverride.id();
            boolean typeMatches = type().equals(otherOverride.type());

            return allowMatches && denyMatches && idMatches && typeMatches;
        }

        return false;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("id", id()).add("type", type().name())
                .add("allow", allow()).add("deny", deny()).toString();
    }

    public enum Type {
        /**
         * A role override.
         */
        ROLE,

        /**
         * A user override.
         */
        USER
    }

}
