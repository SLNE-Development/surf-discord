package dev.slne.discord.discord.interaction.modal

import dev.minn.jda.ktx.coroutines.await
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.modals.Modal

abstract class DiscordBasicModal {

    abstract val id: String

    abstract suspend fun buildModal(): Modal

    abstract suspend fun handleUserSubmitModal(event: ModalInteractionEvent, hook: InteractionHook)

    suspend fun replyModal(event: InteractionHook) {
        if (event is GenericCommandInteractionEvent) {
            event.replyModal(buildModal()).await()
        } else if (event is GenericComponentInteractionCreateEvent) {
            event.replyModal(buildModal()).await()
        }
    }

}