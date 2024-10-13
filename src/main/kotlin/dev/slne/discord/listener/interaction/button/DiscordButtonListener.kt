package dev.slne.discord.listener.interaction.button

import dev.minn.jda.ktx.events.listener
import dev.slne.discord.DiscordBot
import dev.slne.discord.spring.processor.DiscordButtonManager
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

object DiscordButtonListener {
    init {
        DiscordBot.jda.listener<ButtonInteractionEvent> { event ->
            val id = event.button.id ?: return@listener
            DiscordButtonManager.getHandler(id)?.handler?.apply { event.onClick() }
        }
    }
}
