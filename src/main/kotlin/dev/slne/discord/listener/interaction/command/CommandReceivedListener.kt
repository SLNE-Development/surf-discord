package dev.slne.discord.listener.interaction.command

import dev.minn.jda.ktx.events.listener
import dev.slne.discord.discord.interaction.command.DiscordCommandProcessor
import dev.slne.discord.jda
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

object CommandReceivedListener {

    init {
        jda.listener<SlashCommandInteractionEvent> { event ->
            DiscordCommandProcessor.getCommand(event.name)?.command?.execute(event)
                ?: error("Command ${event.name} not found")
        }
    }
}
