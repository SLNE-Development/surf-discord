package dev.slne.discord.ticket;

import java.util.Objects;
import lombok.Builder;
import lombok.ToString;
import net.dv8tion.jda.api.Permission;

import java.util.Collection;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.jetbrains.annotations.Nullable;

/**
 * Create a new permission override for a ticket.
 *
 * @param type  The type of override, either {@link Type#ROLE} or              {@link Type#USER}
 * @param id    The id of the role or user
 * @param allow The permissions to allow
 * @param deny  The permissions to deny
 */
@Builder
public record TicketPermissionOverride(
		Type type,
		long id,
		@Nullable Collection<Permission> allow,
		@Nullable Collection<Permission> deny
) {

  public ChannelAction<TextChannel> addOverride(ChannelAction<TextChannel> action) {
    return switch (type) {
      case ROLE -> action.addRolePermissionOverride(id, allow, deny);
			case USER -> action.addMemberPermissionOverride(id, allow, deny);
    };
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof TicketPermissionOverride that)) {
			return false;
		}

    return id == that.id && type == that.type && Objects.equals(deny, that.deny)
				&& Objects.equals(allow, that.allow);
	}

	@Override
	public int hashCode() {
		int result = Objects.hashCode(type);
		result = 31 * result + Long.hashCode(id);
		result = 31 * result + Objects.hashCode(allow);
		result = 31 * result + Objects.hashCode(deny);
		return result;
	}

	/**
	 * The enum Type.
	 */
	@ToString
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
