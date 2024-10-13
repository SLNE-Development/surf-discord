package dev.slne.discord.discord.interaction.modal.step.creator.unban.step

import dev.minn.jda.ktx.interactions.components.InlineModal
import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.ModalStep
import dev.slne.discord.message.RawMessages
import dev.slne.discord.spring.service.punishment.PunishmentService
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

private const val PUNISHMENT_ID = "punishment-id"

class UnbanTicketPunishmentIdStep : ModalStep() {

    private var punishmentId: String? = null

    override fun InlineModal.buildModalComponents(interaction: StringSelectInteraction) {
        short(
            PUNISHMENT_ID,
            RawMessages.get("modal.unban.step.punishment-id.input.label"),
            placeholder = RawMessages.get("modal.unban.step.punishment-id.input.placeholder"),
            required = true,
            requiredLength = 6..8
        )
    }

    override suspend fun verifyModalInput(event: ModalInteractionEvent) {
        punishmentId = getInput(event, PUNISHMENT_ID)

        if (!PunishmentService.isValidPunishmentId(punishmentId!!)) {
            throw ModalStepInputVerificationException(
                RawMessages.get("modal.unban.step.punishment-id.error.invalid")
            )
        }
    }

    override fun MessageQueue.buildOpenMessages(thread: ThreadChannel) {
        addMessage(
            RawMessages.get(
                "modal.unban.step.punishment-id.messages.punishment-id",
                punishmentId
            )
        )
    }
}
