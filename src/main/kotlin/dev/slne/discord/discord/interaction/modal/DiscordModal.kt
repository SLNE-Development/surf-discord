package dev.slne.discord.discord.interaction.modal

import dev.minn.jda.ktx.coroutines.await
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionComponent
import net.dv8tion.jda.api.interactions.modals.Modal

abstract class DiscordModal protected constructor(
    val customId: String,
    val title: String
) {

    abstract val components: List<ActionComponent>

    abstract fun execute(event: ModalInteractionEvent?)

    private fun buildModal(): Modal {
        val modalBuilder: Modal.Builder = Modal.create(customId, this.title)

        for (component in this.components) {
            modalBuilder.addActionRow(component)
        }

        return modalBuilder.build()
    }

    suspend fun open(event: SlashCommandInteractionEvent): Void =
        event.replyModal(this.buildModal()).await()
}
