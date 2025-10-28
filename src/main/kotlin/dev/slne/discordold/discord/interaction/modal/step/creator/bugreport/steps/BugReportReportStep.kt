package dev.slne.discordold.discord.interaction.modal.step.creator.bugreport.steps

import dev.minn.jda.ktx.interactions.components.InlineModal
import dev.slne.discordold.discord.interaction.modal.step.MessageQueue
import dev.slne.discordold.discord.interaction.modal.step.ModalStep
import dev.slne.discordold.message.translatable
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

private const val REPORT = "report"

class BugReportReportStep(parent: ModalStep) : ModalStep() {
    private var report: String? = null

    override fun InlineModal.buildModalComponents(interaction: StringSelectInteraction) {
        paragraph(
            REPORT,
            translatable("modal.bug-report.step.report.label"),
            placeholder = translatable("modal.bug-report.step.report.placeholder"),
            required = true
        )
    }

    override suspend fun verifyModalInput(event: ModalInteractionEvent) {
        report = event[REPORT]
    }

    override fun MessageQueue.buildOpenMessages(thread: ThreadChannel) {
        addEmptyLine()
        addMessage(translatable("modal.bug-report.step.report.message.title"))
        addMessage("> %s", report)
    }
}