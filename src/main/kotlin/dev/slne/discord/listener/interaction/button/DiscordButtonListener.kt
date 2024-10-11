package dev.slne.discord.listener.interaction.button

import dev.slne.discord.spring.processor.DiscordButtonManager
import dev.slne.discord.spring.processor.DiscordButtonManager.DiscordButtonHandlerHolder
import jakarta.annotation.Nonnull
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

/**
 * The type Discord button listener.
 */
@DiscordListener
class DiscordButtonListener(private val discordButtonProcessor: DiscordButtonManager) :
    ListenerAdapter() {
    override fun onButtonInteraction(@Nonnull event: ButtonInteractionEvent) {
        val holder: DiscordButtonHandlerHolder? = discordButtonProcessor.getHandler(
            event.getButton().getId()!!
        )

        if (holder != null) {
            holder.handler().onClick(event)
        }
    }
}
