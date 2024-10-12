package dev.slne.discord.discord.interaction.modal.step.creator.report.steps

import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.ModalComponentBuilder
import dev.slne.discord.discord.interaction.modal.step.ModalSelectionStep
import dev.slne.discord.discord.interaction.modal.step.StepBuilder
import dev.slne.discord.discord.interaction.modal.step.creator.report.steps.griefing.ReportTicketGriefingStep
import dev.slne.discord.discord.interaction.modal.step.creator.report.steps.player.ReportTicketPlayerStep
import dev.slne.discord.message.RawMessages.Companion.get
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle

private const val OPTION_GRIEFING = "griefing"
private const val OPTION_PLAYER = "player"

private const val REPORTING_PLAYER_NAME_INPUT = "player-name"

class ReportTicketSelectTypeStep : ModalSelectionStep(
    get("modal.report.step.type.selection.title"),
    SelectOption.of(
        get("modal.report.step.type.selection.griefing.label")!!,
        OPTION_GRIEFING
    )
        .withDescription(
            get("modal.report.step.type.selection.griefing.description")
        ),
    SelectOption.of(
        get("modal.report.step.type.selection.player.label")!!,
        OPTION_PLAYER
    )
        .withDescription(
            get("modal.report.step.type.selection.player.description")
        )
) {
    private var playerName: String? = null

    override fun buildModalComponents(builder: ModalComponentBuilder) {
        val label = if (isGriefing) {
            get("modal.report.step.type.input.griefing.own-name")
        } else {
            get("modal.report.step.type.input.report.reported-player")
        }

        builder.addComponent(
            TextInput.create(
                REPORTING_PLAYER_NAME_INPUT,
                label, TextInputStyle.SHORT
            )
                .setRequired(true)
                .setRequiredRange(3, 16)
                .setPlaceholder("Notch")
                .build()
        )
    }

    override suspend fun verifyModalInput(event: ModalInteractionEvent) {
        playerName = getRequiredInput(event, REPORTING_PLAYER_NAME_INPUT)
    }

    override fun buildOpenMessages(messages: MessageQueue, thread: ThreadChannel) {
        messages.addEmptyLine()
        messages.addMessage(get("modal.report.step.type.messages.player-name", playerName))
    }

    override fun buildChildSteps(): StepBuilder {
        return if (isGriefing) {
            StepBuilder.startWith(ReportTicketGriefingStep())
        } else if (isPlayer) {
            StepBuilder.startWith(ReportTicketPlayerStep())
        } else {
            StepBuilder.empty()
        }
    }

    private val isGriefing: Boolean
        get() = OPTION_GRIEFING == selected

    private val isPlayer: Boolean
        get() = OPTION_PLAYER == selected
}
