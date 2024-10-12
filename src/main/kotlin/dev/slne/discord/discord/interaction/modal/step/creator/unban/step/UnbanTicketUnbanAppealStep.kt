package dev.slne.discord.discord.interaction.modal.step.creator.unban.step

import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.ModalComponentBuilder
import dev.slne.discord.discord.interaction.modal.step.ModalStep
import dev.slne.discord.message.RawMessages.Companion.get
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle

private const val COMPONENT_ID = "unban-appeal"

class UnbanTicketUnbanAppealStep(parent: ModalStep?) : ModalStep() {

    private var unbanAppeal: String? = null

    override fun buildModalComponents(builder: ModalComponentBuilder) {
        builder.addComponent(
            TextInput.create(
                COMPONENT_ID,
                get("modal.unban.step.appeal.input.appeal.label")!!,
                TextInputStyle.PARAGRAPH
            )
                .setRequired(true)
                .setMinLength(300)
                .setPlaceholder(
                    get("modal.unban.step.appeal.input.appeal.placeholder")
                )
                .build()
        )
    }

    override suspend fun verifyModalInput(event: ModalInteractionEvent) {
        unbanAppeal = getRequiredInput(event, COMPONENT_ID)
    }

    override fun buildOpenMessages(messages: MessageQueue, thread: ThreadChannel) {
        messages.addMessage(get("modal.unban.step.appeal.messages.appeal.title"))
        messages.addMessage(get("modal.unban.step.appeal.messages.appeal", unbanAppeal))
    }
}
