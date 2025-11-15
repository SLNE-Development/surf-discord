package dev.slne.discord.discord.interaction.modal.step.creator.bugreport.steps

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

private const val REPORT = "report"

class BugReportReportStep(parent: ModalStep) : ModalStep() {
    private var report: String? = null

    override fun buildModalComponents(builder: Modal.Builder, interaction: StringSelectInteraction) {
        builder.addComponents(
            Label.of(
                translatable("modal.bug-report.step.report.label"),
                TextInput.create(REPORT, TextInputStyle.PARAGRAPH)
                    .setPlaceholder(translatable("modal.bug-report.step.report.placeholder"))
                    .setRequired(true)
                    .build()
            )
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