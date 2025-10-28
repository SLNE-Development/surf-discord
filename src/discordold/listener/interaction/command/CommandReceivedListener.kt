package dev.slne.discordold.listener.interaction.command

import dev.minn.jda.ktx.events.listener
import dev.slne.discordold.discord.interaction.command.DiscordCommandProcessor
import jakarta.annotation.PostConstruct
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component

@Component
class CommandReceivedListener(
    private val jda: JDA,
    private val processor: DiscordCommandProcessor
) {

    @PostConstruct
    fun registerListener() {
        jda.listener<SlashCommandInteractionEvent> { event ->
            processor.getCommand(event.name)?.second?.execute(event)
                ?: error("Command ${event.name} not found")
        }
    }
}
