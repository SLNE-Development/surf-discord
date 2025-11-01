package dev.slne.surf.discord.contextmenu

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent

interface MessageContextCommand {
    suspend fun execute(event: MessageContextInteractionEvent)
}