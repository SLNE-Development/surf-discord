package dev.slne.discord.persistence.converter

import dev.slne.discord.DiscordBot
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel

@Converter(autoApply = true)
class DiscordThreadChannelConverter : AttributeConverter<ThreadChannel, String> {
    override fun convertToDatabaseColumn(thread: ThreadChannel?): String {
        return thread?.id ?: ""
    }

    override fun convertToEntityAttribute(id: String?): ThreadChannel? {
        return id?.let { DiscordBot.jda.getThreadChannelById(id) }
    }
}