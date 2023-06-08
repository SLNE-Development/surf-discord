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

        guilds.add(new DiscordGuild("449314616628084758", "983429475649876029", "983492109992595456",
                "999746272368017509"));

        return guilds;
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
     * @return  The guild.
     */
    public static DiscordGuild getGuild(Guild guild) {
        return getGuild(guild.getId());
    }

}
