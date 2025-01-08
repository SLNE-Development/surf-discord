package dev.slne.discord.listener.interaction.command

import dev.minn.jda.ktx.events.listener
import dev.slne.discord.discord.interaction.command.DiscordCommandProcessor
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class CommandReceivedListener(jda: JDA, processor: DiscordCommandProcessor) {

    init {
        jda.listener<SlashCommandInteractionEvent> { event ->
            processor.getCommand(event.name)?.second?.execute(event)
                ?: error("Command ${event.name} not found")
        }
    }
}
