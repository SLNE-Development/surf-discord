package dev.slne.discord.discord.interaction.modal.step.creator.unban.step

import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.ModalStep
import dev.slne.discord.message.translatable
import net.dv8tion.jda.api.components.label.Label
import net.dv8tion.jda.api.components.textinput.TextInput
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction
import net.dv8tion.jda.api.modals.Modal

private const val COMPONENT_ID = "unban-appeal"

class UnbanTicketUnbanAppealStep(parent: ModalStep) : ModalStep() {

    private var unbanAppeal: String? = null

    override fun buildModalComponents(builder: Modal.Builder, interaction: StringSelectInteraction) {
        builder.addComponents(
            Label.of(
                translatable("modal.unban.step.appeal.input.appeal.label"),
                TextInput.create(COMPONENT_ID, TextInputStyle.PARAGRAPH)
                    .setRequired(true)
                    .setMinLength(300)
                    .setPlaceholder(translatable("modal.unban.step.appeal.input.appeal.placeholder"))
                    .build()
            )
        )
    }

    override suspend fun verifyModalInput(event: ModalInteractionEvent) {
        unbanAppeal = getInput(event, COMPONENT_ID)
    }

    override fun MessageQueue.buildOpenMessages(thread: ThreadChannel) {
        addMessage(translatable("modal.unban.step.appeal.messages.appeal.title"))
        addMessage(translatable("modal.unban.step.appeal.messages.appeal", unbanAppeal))
    }
}
