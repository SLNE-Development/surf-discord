package dev.slne.discord.discord.guild.role;

import dev.slne.discord.discord.guild.role.roles.DefaultDiscordRole;
import dev.slne.discord.discord.guild.role.roles.DiscordAdminDiscordRole;
import dev.slne.discord.discord.guild.role.roles.DiscordModDiscordRole;
import dev.slne.discord.discord.guild.role.roles.ServerAdminDiscordRole;
import dev.slne.discord.discord.guild.role.roles.ServerModDiscordRole;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Discord role manager.
 */
public class DiscordRoleManager {

	private final List<DiscordRole> roles;

	/**
	 * Instantiates a new Discord role manager.
	 */
	public DiscordRoleManager() {
		this.roles = new ArrayList<>();

		this.roles.add(new DiscordAdminDiscordRole());
		this.roles.add(new DiscordModDiscordRole());
		this.roles.add(new ServerAdminDiscordRole());
		this.roles.add(new ServerModDiscordRole());

		this.roles.add(new DefaultDiscordRole());
	}

	/**
	 * Returns the role with the given name or null if no role with the
	 *
	 * @param name the name of the role
	 *
	 * @return the role with the given name or null if no role with the
	 */
	public DiscordRole getRoleByName(String name) {
		return this.roles.stream().filter(role -> role.getName().equals(name)).findFirst().orElse(null);
	}

}
