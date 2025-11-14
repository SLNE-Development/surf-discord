package dev.slne.surf.discord.interaction.button

import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

interface DiscordButton {
    val id: String
    val button: Button

    suspend fun onClick(event: ButtonInteractionEvent) {}
}
