package dev.slne.discord.discord.guild;

import dev.slne.discord.discord.guild.reactionrole.ReactionRoleConfig;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.List;

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
//        return new DiscordGuild("449314616628084758", "983429475649876029", "449314761386098688",
//                "449314761386098688", "998912043862208532", "998912043862208532", "1052580474712756244",
//                new ReactionRoleConfig("1124375143645454477", "983450492862595122", "U+1F514",
//                        "449980058120093706"));
        return new DiscordGuild("449314616628084758", "983429475649876029", "449314761386098688",
                "449314761386098688", "449314761386098688", "449314761386098688", "1052580474712756244",
                new ReactionRoleConfig("1124375143645454477", "983450492862595122", "U+1F514",
                        "449980058120093706"));
    }

    /**
     * Returns the cast crafter guild
     *
     * @return The cast crafter guild
     */
    private static DiscordGuild getCastCrafterGuild() {
        return new DiscordGuild("133198459531558912", "1124438557830955018", "156159112416067584",
                "949704206888079490", "156164562499010560", "1001166778287792158", "1052361599484170300",
                new ReactionRoleConfig("1052600279968784466", "1005106900179894333", "U+1F514",
                        "1052595432913637469"));
    }

    /**
     * Returns a guild by its id.
     *
     * @param guildId The id of the guild.
     *
     * @return The guild.
     */
    public static DiscordGuild getGuild(String guildId) {
        return getGuilds().stream().filter(guild -> guild.getGuildId().equals(guildId)).findFirst().orElse(null);
    }

    /**
     * Returns a guild by its id.
     *
     * @param guild The guild.
     *
     * @return The guild.
     */
    public static DiscordGuild getGuild(Guild guild) {
        return getGuild(guild.getId());
    }

}
