package dev.slne.surf.discord.interaction.button

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button

interface DiscordButton {
    val id: String
    val button: Button

    suspend fun onClick(event: ButtonInteractionEvent) {}
}
