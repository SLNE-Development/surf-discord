package dev.slne.discord.discord.interaction.button

import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction

interface DiscordButtonHandler {
    suspend fun ButtonInteraction.onClick()
}
