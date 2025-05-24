package dev.slne.discord.discord.interaction.modal.step.creator.report.steps.player

import dev.minn.jda.ktx.interactions.components.InlineModal
import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.ModalStep
import dev.slne.discord.message.translatable
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

private const val REPORT_PLAYER_REASON_INPUT = "report-player-reason"
private const val REPORT_PLAYER_NAME = "report-player-name"

class ReportTicketPlayerStep : ModalStep() {

    private var reportPlayerName: String? = null
    private var reportReason: String? = null

    override fun InlineModal.buildModalComponents(interaction: StringSelectInteraction) {
        short(
            REPORT_PLAYER_NAME,
            translatable("modal.report.step.player.input.reporting-player.label"),
            required = true,
            requiredLength = 3..16,
        )

        paragraph(
            REPORT_PLAYER_REASON_INPUT,
            translatable("modal.report.step.player.input.reason.label"),
            required = true,
            placeholder = translatable("modal.report.step.player.input.reason.placeholder"),
        ) { minLength = 20 }
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
