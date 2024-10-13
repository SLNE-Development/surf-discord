package dev.slne.discord.discord.interaction.modal.step.creator.unban.step

import dev.minn.jda.ktx.interactions.components.InlineModal
import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.ModalStep
import dev.slne.discord.message.RawMessages.Companion.get
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent

private const val COMPONENT_ID = "unban-appeal"

class UnbanTicketUnbanAppealStep(parent: ModalStep) : ModalStep() {

    private var unbanAppeal: String? = null

    override fun InlineModal.buildModalComponents() {
        paragraph(
            COMPONENT_ID,
            get("modal.unban.step.appeal.input.appeal.label"),
            required = true,
            placeholder = get("modal.unban.step.appeal.input.appeal.placeholder"),
        ) { minLength = 300 }
    }

    override suspend fun verifyModalInput(event: ModalInteractionEvent) {
        unbanAppeal = getInput(event, COMPONENT_ID)
    }

    override fun MessageQueue.buildOpenMessages(thread: ThreadChannel) {
        addMessage(get("modal.unban.step.appeal.messages.appeal.title"))
        addMessage(get("modal.unban.step.appeal.messages.appeal", unbanAppeal))
    }
}
