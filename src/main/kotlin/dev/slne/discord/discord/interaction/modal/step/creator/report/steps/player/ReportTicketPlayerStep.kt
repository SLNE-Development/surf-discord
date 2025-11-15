package dev.slne.discord.discord.interaction.modal.step.creator.report.steps.player

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

private const val REPORT_PLAYER_REASON_INPUT = "report-player-reason"
private const val REPORT_PLAYER_NAME = "report-player-name"

class ReportTicketPlayerStep : ModalStep() {

    private var reportPlayerName: String? = null
    private var reportReason: String? = null

    override fun buildModalComponents(builder: Modal.Builder, interaction: StringSelectInteraction) {
        builder.addComponents(
            Label.of(
                translatable("modal.report.step.player.input.reporting-player.label"),
                TextInput.create(REPORT_PLAYER_NAME, TextInputStyle.SHORT)
                    .setRequired(true)
                    .setRequiredRange(3, 16)
                    .build()
            ),
            Label.of(
                translatable("modal.report.step.player.input.reason.label"),
                TextInput.create(REPORT_PLAYER_REASON_INPUT, TextInputStyle.PARAGRAPH)
                    .setPlaceholder(translatable("modal.report.step.player.input.reason.placeholder"))
                    .setRequired(true)
                    .setMinLength(20)
                    .build()
            )
        )
    }

    override suspend fun verifyModalInput(event: ModalInteractionEvent) {
        reportPlayerName = event[REPORT_PLAYER_NAME]
        reportReason = event[REPORT_PLAYER_REASON_INPUT]
    }

    override fun MessageQueue.buildOpenMessages(thread: ThreadChannel) {
        addMessage(
            translatable("modal.report.step.player.messages.reporting-player", reportPlayerName)
        )
        addEmptyLine()
        addMessage(
            "# " + translatable("modal.report.step.player.messages.reported-player.reason")
        )
        addMessage("> %s", reportReason)
    }
}
