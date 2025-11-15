package dev.slne.discord.listener.interaction.button

import dev.minn.jda.ktx.events.listener
import dev.slne.discord.discord.interaction.button.DiscordButtonProcessor
import jakarta.annotation.PostConstruct
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import org.springframework.stereotype.Component

@Component
class DiscordButtonListener(private val jda: JDA, private val processor: DiscordButtonProcessor) {

    @PostConstruct
    fun registerListener() {
        jda.listener<ButtonInteractionEvent> { event ->
            val id = event.button.customId ?: return@listener
            processor.getHandler(id)?.second?.apply { event.onClick() }
        }
    }
}
