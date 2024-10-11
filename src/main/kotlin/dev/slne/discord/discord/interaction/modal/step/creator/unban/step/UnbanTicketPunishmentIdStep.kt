package dev.slne.discord.discord.interaction.modal.step.creator.unban.step

import dev.slne.discord.Bootstrap
import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import feign.FeignException
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import java.util.concurrent.CompletionException

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

    @Throws(ModalStepInputVerificationException::class)
    override fun verifyModalInput(event: ModalInteractionEvent) {
        punishmentId = getRequiredInput(event, PUNISHMENT_ID)

        try {
            if (!PUNISHMENT_SERVICE.get().isValidPunishmentId(punishmentId).join()) {
                handlePunishmentNotFound()
            }
        } catch (e: CompletionException) {
            if (e.cause is FeignException.NotFound) {
                handlePunishmentNotFound()
            } else {
                LOGGER.error("Error while fetching ban by punishment ID", e)
                throw ModalStepInputVerificationException(RawMessages.get("error.generic"))
            }
        }
    }

    override fun buildOpenMessages(messages: MessageQueue, channel: TextChannel?) {
        messages.addMessage(
            RawMessages.get("modal.unban.step.punishment-id.messages.punishment-id", punishmentId)
        )
    }

    companion object {
        private val LOGGER: ComponentLogger = ComponentLogger.logger()
        private const val PUNISHMENT_ID = "punishment-id"
        private val PUNISHMENT_SERVICE: Lazy<PunishmentService> = Lazy.of {
            Bootstrap.getContext().getBean(
                PunishmentService::class.java
            )
        }

        @Throws(ModalStepInputVerificationException::class)
        private fun handlePunishmentNotFound() {
            throw ModalStepInputVerificationException(
                RawMessages.get("modal.unban.step.punishment-id.error.invalid")
            )
        }
    }
}
