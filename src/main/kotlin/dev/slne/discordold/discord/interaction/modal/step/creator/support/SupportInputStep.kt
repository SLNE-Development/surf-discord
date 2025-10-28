package dev.slne.discordold.discord.interaction.modal.step.creator.support

import dev.minn.jda.ktx.interactions.components.InlineModal
import dev.slne.discordold.discord.interaction.modal.step.MessageQueue
import dev.slne.discordold.discord.interaction.modal.step.ModalStep
import dev.slne.discordold.message.translatable
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

private const val INPUT = "input"

class SupportInputStep : ModalStep() {
    private var input: String? = null

    override fun InlineModal.buildModalComponents(interaction: StringSelectInteraction) {
        paragraph(
            INPUT,
            translatable("modal.support.step.input.label"),
            placeholder = translatable("modal.support.step.input.placeholder"),
            required = true,
        )
    }

    override suspend fun verifyModalInput(event: ModalInteractionEvent) {
        input = event[INPUT]
    }

    override fun MessageQueue.buildOpenMessages(thread: ThreadChannel) {
        addEmptyLine()
        addMessage(translatable("modal.support.step.input.message"))
        addMessage("> %s", input)
    }
}