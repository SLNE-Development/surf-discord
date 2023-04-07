package dev.slne.discord.message;

import java.time.Instant;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class MessageManager {

    public static MessageEmbed getErrorEmbed(String title, String description) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle(title);
        embedBuilder.setDescription(description);
        embedBuilder.setColor(0xff0000);
        embedBuilder.setTimestamp(Instant.now());

        return embedBuilder.build();
    }

}
