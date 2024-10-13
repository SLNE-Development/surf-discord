package dev.slne.discord.listener.interaction.command

import dev.minn.jda.ktx.events.listener
import dev.slne.discord.DiscordBot
import dev.slne.discord.discord.interaction.command.DiscordCommandManager
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

object CommandReceivedListener {

    init {
        DiscordBot.jda.listener<SlashCommandInteractionEvent> { event ->
            DiscordCommandManager.getCommand(event.name)?.command?.execute(event)
                ?: error("Command ${event.name} not found")
        }

    }
}
