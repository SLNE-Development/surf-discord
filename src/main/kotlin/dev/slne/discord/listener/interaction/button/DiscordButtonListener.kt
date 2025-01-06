package dev.slne.discord.listener.interaction.button

import dev.minn.jda.ktx.events.listener
import dev.slne.discord.discord.interaction.button.DiscordButtonManager
import dev.slne.discord.jda
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

object DiscordButtonListener {
    init {
        jda.listener<ButtonInteractionEvent> { event ->
            val id = event.button.id ?: return@listener
            DiscordButtonManager.getHandler(id)?.handler?.apply { event.onClick() }
        }
    }
}
