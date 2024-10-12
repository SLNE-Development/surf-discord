package dev.slne.discord.discord.interaction.modal.step.creator.report.steps.player

import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.ModalComponentBuilder
import dev.slne.discord.discord.interaction.modal.step.ModalStep
import dev.slne.discord.message.RawMessages.Companion.get
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle

private const val REPORT_PLAYER_REASON_INPUT = "report-player-reason"
private const val REPORT_PLAYER_NAME = "report-player-name"

class ReportTicketPlayerStep : ModalStep() {

    private var reportPlayerName: String? = null
    private var reportReason: String? = null

    override fun buildModalComponents(builder: ModalComponentBuilder) {
        builder.addFirstComponent(
            TextInput.create(
                REPORT_PLAYER_NAME,
                get("modal.report.step.player.input.reporting-player.label")!!,
                TextInputStyle.SHORT
            )
                .setRequired(true)
                .build()
        )
        builder.addComponent(
            TextInput.create(
                REPORT_PLAYER_REASON_INPUT,
                get("modal.report.step.player.input.reason.label")!!,
                TextInputStyle.PARAGRAPH
            )
                .setRequired(true)
                .setPlaceholder(get("modal.report.step.player.input.reason.placeholder"))
                .setMinLength(20)
                .build()
        )
    }

    override suspend fun verifyModalInput(event: ModalInteractionEvent) {
        reportPlayerName = getRequiredInput(event, REPORT_PLAYER_NAME)
        reportReason = getRequiredInput(event, REPORT_PLAYER_REASON_INPUT)
    }

    override fun buildOpenMessages(messages: MessageQueue, thread: ThreadChannel) {
        messages.addMessage(
            get("modal.report.step.player.messages.reporting-player", reportPlayerName)!!
        )
        messages.addEmptyLine()
        messages.addMessage(
            "# " + get("modal.report.step.player.input.reason.placeholder")
        )
        messages.addMessage("> %s", reportReason)
    }
}
