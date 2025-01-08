package dev.slne.discord.persistence.converter

import dev.slne.discord.getBean
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild

@Converter(autoApply = true)
class DiscordGuildConverter : AttributeConverter<Guild, String> {
    override fun convertToDatabaseColumn(guild: Guild?): String {
        return guild?.id ?: ""
    }

    override fun convertToEntityAttribute(id: String?): Guild? {
        return id?.let { getBean<JDA>().getGuildById(id) }
    }
}