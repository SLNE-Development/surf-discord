package dev.slne.discord.discord.interaction.button

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

interface DiscordButtonHandler {
    suspend fun onClick(event: ButtonInteractionEvent)
}
