package dev.slne.surf.discord.interaction.selectmenu

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu

interface DiscordSelectMenu {
    val id: String
    suspend fun create(hook: InteractionHook): StringSelectMenu

    suspend fun onSelect(event: StringSelectInteractionEvent) {}
}
