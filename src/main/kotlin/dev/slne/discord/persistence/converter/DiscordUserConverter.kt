package dev.slne.discord.persistence.converter

import dev.slne.discord.DiscordBot
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.dv8tion.jda.api.entities.User

@Converter(autoApply = true)
class DiscordUserConverter : AttributeConverter<User, String> {
    override fun convertToDatabaseColumn(user: User?): String {
        return user?.id ?: ""
    }

    // FIXME: 15.10.2024 09:38  this is a blocking call, is this possible due to the save function being suspended or does this need to be somehow converted
    override fun convertToEntityAttribute(id: String?): User? {
        return id?.let { DiscordBot.jda.retrieveUserById(id).complete() }
    }
}