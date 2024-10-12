package dev.slne.discord.discord.interaction.modal.step.creator.unban.step

import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.ModalComponentBuilder
import dev.slne.discord.discord.interaction.modal.step.ModalStep
import dev.slne.discord.message.RawMessages
import dev.slne.discord.spring.service.punishment.PunishmentService
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle

private const val PUNISHMENT_ID = "punishment-id"

class UnbanTicketPunishmentIdStep : ModalStep() {

    private var punishmentId: String? = null

    override fun buildModalComponents(builder: ModalComponentBuilder) {
        builder.addComponent(
            TextInput.create(
                PUNISHMENT_ID,
                RawMessages.get("modal.unban.step.punishment-id.input.label"),
                TextInputStyle.SHORT
            )
                .setPlaceholder(RawMessages.get("modal.unban.step.punishment-id.input.placeholder"))
                .setRequired(true)
                .setMinLength(6)
                .setMaxLength(8)
                .build()
        )
    }

    override suspend fun verifyModalInput(event: ModalInteractionEvent) {
        punishmentId = getRequiredInput(event, PUNISHMENT_ID)

        if (!PunishmentService.isValidPunishmentId(punishmentId!!)) {
            throw ModalStepInputVerificationException(
                RawMessages.get("modal.unban.step.punishment-id.error.invalid")
            )
        }
    }

    override fun buildOpenMessages(messages: MessageQueue, thread: ThreadChannel) {
        messages.addMessage(
            RawMessages.get("modal.unban.step.punishment-id.messages.punishment-id", punishmentId)
        )
    }
}
