package dev.slne.discord.discord.guild;

import java.util.ArrayList;
import java.util.List;

import dev.slne.discord.discord.guild.reactionrole.ReactionRoleConfig;
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

        discordSupportAdmins.add("1080526283462676584"); // Arty
        serverSupportAdmins.add("180623182594572288"); // Ammo

        serverSupportModerators.add("201843528618213376"); // Keviro

        ReactionRoleConfig rrConfig = new ReactionRoleConfig("1124375143645454477", "983450492862595122", "U+1F514",
                "449980058120093706");

        return new DiscordGuild("449314616628084758", "983429475649876029", discordSupportAdmins,
                serverSupportAdmins, discordSupportModerators, serverSupportModerators, "1052580474712756244",
                rrConfig);
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

        discordSupportAdmins.add("1080526283462676584"); // Arty
        discordSupportAdmins.add("159372579331768320"); // Tjorben
        discordSupportAdmins.add("128876960238665728"); // CastCrafter
        discordSupportAdmins.add("369349926317981696"); // MelanX

        discordSupportModerators.add("395238525517168641"); // Finja
        discordSupportModerators.add("235079227085553676"); // Laura

        serverSupportAdmins.add("180623182594572288"); // Ammo
        serverSupportAdmins.add("201843528618213376"); // Keviro

        serverSupportModerators.add("570544431174778881"); // Kendor
        serverSupportModerators.add("697395817882845214"); // Twisti

        ReactionRoleConfig rrConfig = new ReactionRoleConfig("1052600279968784466", "1005106900179894333", "U+1F514",
                "1052595432913637469");

        return new DiscordGuild("133198459531558912", "1124438557830955018", discordSupportAdmins,
                serverSupportAdmins, discordSupportModerators, serverSupportModerators, "1052361599484170300",
                rrConfig);
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
