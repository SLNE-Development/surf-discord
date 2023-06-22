package dev.slne.discord.discord.guild;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;

public class DiscordGuilds {

    /**
     * Private constructor to prevent instantiation.
     */
    private DiscordGuilds() {
    }

    /**
     * Returns the guilds.
     *
     * @return The guilds.
     */
    public static List<DiscordGuild> getGuilds() {
        List<DiscordGuild> guilds = new ArrayList<>();

        guilds.add(getDevGuild());
        guilds.add(getCastCrafterGuild());

        return guilds;
    }

    /**
     * Returns the dev guild
     *
     * @return The dev guild
     */
    private static DiscordGuild getDevGuild() {
        List<String> discordSupportAdmins = new ArrayList<>();
        List<String> serverSupportAdmins = new ArrayList<>();
        List<String> discordSupportModerators = new ArrayList<>();
        List<String> serverSupportModerators = new ArrayList<>();

        serverSupportAdmins.add("201843528618213376");

        serverSupportModerators.add("570544431174778881");

        return new DiscordGuild("449314616628084758", "983429475649876029", discordSupportAdmins,
                serverSupportAdmins, discordSupportModerators, serverSupportModerators);
    }

    /**
     * Returns the cast crafter guild
     *
     * @return The cast crafter guild
     */
    private static DiscordGuild getCastCrafterGuild() {
        List<String> discordSupportAdmins = new ArrayList<>();
        List<String> serverSupportAdmins = new ArrayList<>();
        List<String> discordSupportModerators = new ArrayList<>();
        List<String> serverSupportModerators = new ArrayList<>();

        discordSupportAdmins.add("983430264959164446");
        discordSupportAdmins.add("159372579331768320");
        discordSupportAdmins.add("128876960238665728");
        discordSupportAdmins.add("369349926317981696");

        discordSupportModerators.add("395238525517168641");
        discordSupportModerators.add("235079227085553676");

        serverSupportAdmins.add("180623182594572288");
        serverSupportAdmins.add("201843528618213376");

        serverSupportModerators.add("570544431174778881");
        serverSupportModerators.add("697395817882845214");

        return new DiscordGuild("133198459531558912", "987072606530322522", discordSupportAdmins,
                serverSupportAdmins, discordSupportModerators, serverSupportModerators);
    }

    /**
     * Returns a guild by its id.
     *
     * @param guildId The id of the guild.
     * @return The guild.
     */
    public static DiscordGuild getGuild(String guildId) {
        return getGuilds().stream().filter(guild -> guild.getGuildId().equals(guildId)).findFirst().orElse(null);
    }

    /**
     * Returns a guild by its id.
     *
     * @param guild The guild.
     * @return The guild.
     */
    public static DiscordGuild getGuild(Guild guild) {
        return getGuild(guild.getId());
    }

}
