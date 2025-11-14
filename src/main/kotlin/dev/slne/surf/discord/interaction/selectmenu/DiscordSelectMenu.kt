package dev.slne.surf.discord.interaction.selectmenu

import net.dv8tion.jda.api.components.selections.SelectMenu
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook

interface DiscordSelectMenu {
    val id: String
    suspend fun create(hook: InteractionHook): SelectMenu

    suspend fun onSelect(event: StringSelectInteractionEvent) {}
}
