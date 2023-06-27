package dev.slne.discord.discord.guild.role;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dev.slne.discord.discord.guild.role.roles.DiscordAdminDiscordRole;
import dev.slne.discord.discord.guild.role.roles.DiscordModDiscordRole;
import dev.slne.discord.discord.guild.role.roles.ServerAdminDiscordRole;
import dev.slne.discord.discord.guild.role.roles.ServerModDiscordRole;

public class DiscordRoleManager {

    private List<DiscordRole> roles;

    public DiscordRoleManager() {
        this.roles = new ArrayList<>();

        this.roles.add(new DiscordAdminDiscordRole());
        this.roles.add(new DiscordModDiscordRole());
        this.roles.add(new ServerAdminDiscordRole());
        this.roles.add(new ServerModDiscordRole());
    }

    /**
     * Returns the role with the given name or an empty optional if no role with the
     *
     * @param name the name of the role
     * @return the role with the given name or an empty optional if no role with the
     */
    public Optional<DiscordRole> getRoleByName(String name) {
        return this.roles.stream().filter(role -> role.getName().equals(name)).findFirst();
    }

    /**
     * @return the roles
     */
    public List<DiscordRole> getRoles() {
        return roles;
    }

}
