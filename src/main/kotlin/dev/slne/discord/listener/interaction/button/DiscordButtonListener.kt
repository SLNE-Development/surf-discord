package dev.slne.discord.listener.interaction.button

import dev.minn.jda.ktx.events.listener
import dev.slne.discord.discord.interaction.button.DiscordButtonProcessor
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

class DiscordButtonListener(jda: JDA, processor: DiscordButtonProcessor) {
    init {
        jda.listener<ButtonInteractionEvent> { event ->
            val id = event.button.id ?: return@listener
            processor.getHandler(id)?.second?.apply { event.onClick() }
        }
    }
}
