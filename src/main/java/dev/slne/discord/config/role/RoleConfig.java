package dev.slne.discord.config.role;

import dev.slne.discord.config.discord.GuildConfig;
import dev.slne.discord.guild.permission.CommandPermission;
import dev.slne.discord.guild.permission.DiscordPermission;
import dev.slne.discord.ticket.TicketType;
import lombok.Getter;
import lombok.ToString;
import net.dv8tion.jda.api.Permission;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

/**
 * The interface Role config.
 */
@ConfigSerializable
@ToString(doNotUseGetters = true)
@Getter
public class RoleConfig {

	private List<String> discordRoleIds;

	private List<DiscordPermission> discordAllowedPermissions;
	private List<DiscordPermission> discordDeniedPermissions;
	private List<CommandPermission> commandAllowedPermissions;
	private List<CommandPermission> commandDeniedPermissions;

	private boolean defaultRole;

	/**
	 * Instantiates a new Role config.
	 */
	public RoleConfig() {
	}

	/**
	 * Gets config.
	 *
	 * @param guildId  the guild id
	 * @param roleName the role name
	 *
	 * @return the config
	 */
	public static RoleConfig getConfig(String guildId, String roleName) {
		return GuildConfig.getByGuildId(guildId).getRoleConfig().get(roleName);
	}

	/**
	 * Gets discord role roles.
	 *
	 * @param guildId the guild id
	 * @param roleId  the role id
	 *
	 * @return the discord role roles
	 */
	public static List<RoleConfig> getDiscordRoleRoles(String guildId, String roleId) {
		return GuildConfig.getByGuildId(guildId).getRoleConfig().values().stream()
						  .filter(roleConfig -> roleConfig.getDiscordRoleIds().contains(roleId)).toList();
	}

	/**
	 * Gets default role.
	 *
	 * @param guildId the guild id
	 *
	 * @return the default role
	 */
	public static RoleConfig getDefaultRole(String guildId) {
		return GuildConfig.getByGuildId(guildId).getRoleConfig().values().stream()
						  .filter(RoleConfig::isDefaultRole)
						  .findFirst()
						  .orElse(null);
	}

	/**
	 * Has command permission boolean.
	 *
	 * @param commandPermission the command permission
	 *
	 * @return the boolean
	 */
	public boolean hasCommandPermission(CommandPermission commandPermission) {
		return commandAllowedPermissions.contains(commandPermission) &&
			   !commandDeniedPermissions.contains(commandPermission);
	}

	/**
	 * Has discord permission boolean.
	 *
	 * @param permission the permission
	 *
	 * @return the boolean
	 */
	public boolean hasDiscordPermission(DiscordPermission permission) {
		return discordAllowedPermissions.contains(permission) &&
			   !discordDeniedPermissions.contains(permission);
	}

	/**
	 * Can view ticket type boolean.
	 *
	 * @param ticketType the ticket type
	 *
	 * @return the boolean
	 */
	public boolean canViewTicketType(TicketType ticketType) {
		return discordAllowedPermissions.contains(ticketType.getViewPermission());
	}

	/**
	 * Gets discord permissions as jda.
	 *
	 * @return the discord permissions as jda
	 */
	public List<Permission> getDiscordPermissionsAsJDA() {
		return discordAllowedPermissions.stream().map(DiscordPermission::getPermission).toList();
	}

	/**
	 * Gets discord denied permissions as jda.
	 *
	 * @return the discord denied permissions as jda
	 */
	public List<Permission> getDiscordDeniedPermissionsAsJDA() {
		return discordDeniedPermissions.stream().map(DiscordPermission::getPermission).toList();
	}

}
