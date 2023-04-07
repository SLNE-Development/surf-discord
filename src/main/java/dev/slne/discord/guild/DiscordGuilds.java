package dev.slne.discord.guild;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;

public class DiscordGuilds {

    public static List<DiscordGuild> getGuilds() {
        List<DiscordGuild> guilds = new ArrayList<>();

        guilds.add(new DiscordGuild("449314616628084758", "983429475649876029", "983492109992595456",
                "999746272368017509"));

        return guilds;
    }

    public static DiscordGuild getGuild(String guildId) {
        return getGuilds().stream().filter(guild -> guild.getGuildId().equals(guildId)).findFirst().orElse(null);
    }

    public static DiscordGuild getGuild(Guild guild) {
        return getGuild(guild.getId());
    }

}
