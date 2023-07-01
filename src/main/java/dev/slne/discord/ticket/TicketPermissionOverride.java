package dev.slne.discord.ticket;

import java.util.Collection;

import com.google.common.base.MoreObjects;

import net.dv8tion.jda.api.Permission;

public class TicketPermissionOverride {

    private final Type type;
    private final long id;
    private final Collection<Permission> allow;
    private final Collection<Permission> deny;

    /**
     * Create a new permission override for a ticket.
     *
     * @param type  The type of override, either {@link Type#ROLE} or
     *              {@link Type#USER}
     * @param id    The id of the role or user
     * @param allow The permissions to allow
     * @param deny  The permissions to deny
     */
    public TicketPermissionOverride(Type type, long id, Collection<Permission> allow, Collection<Permission> deny) {
        this.type = type;
        this.id = id;
        this.allow = allow;
        this.deny = deny;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (other == null) {
            return false;
        }

        if (other instanceof TicketPermissionOverride otherOverride) {
            boolean allowMatches = getAllow().equals(otherOverride.getAllow());
            boolean denyMatches = getDeny().equals(otherOverride.getDeny());
            boolean idMatches = getId() == otherOverride.getId();
            boolean typeMatches = getType().equals(otherOverride.getType());

            return allowMatches && denyMatches && idMatches && typeMatches;
        }

        return false;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("id", getId()).add("type", getType().name())
                .add("allow", getAllow()).add("deny", getDeny()).toString();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * @return the allow
     */
    public Collection<Permission> getAllow() {
        return allow;
    }

    /**
     * @return the deny
     */
    public Collection<Permission> getDeny() {
        return deny;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * Get the type of override.
     */
    public enum Type {
        ROLE,
        USER
    }

}
