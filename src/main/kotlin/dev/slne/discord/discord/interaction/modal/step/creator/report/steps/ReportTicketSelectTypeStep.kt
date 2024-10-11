package dev.slne.discord.discord.interaction.modal.step.creator.report.steps

import dev.slne.discord.discord.interaction.modal.step.*
import dev.slne.discord.discord.interaction.modal.step.creator.report.steps.griefing.ReportTicketGriefingStep
import dev.slne.discord.discord.interaction.modal.step.creator.report.steps.player.ReportTicketPlayerStep
import dev.slne.discord.message.RawMessages.Companion.get
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle

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
                label!!, TextInputStyle.SHORT
            )
                .setRequired(true)
                .setRequiredRange(3, 16)
                .setPlaceholder("Notch")
                .build()
        )
    }

    @Throws(ModalStepInputVerificationException::class)
    override fun verifyModalInput(event: ModalInteractionEvent) {
        playerName = getRequiredInput(event, REPORTING_PLAYER_NAME_INPUT)
    }

    override fun buildOpenMessages(messages: MessageQueue, channel: TextChannel?) {
        messages.addEmptyLine()
        messages.addMessage(get("modal.report.step.type.messages.player-name", playerName)!!)
    }

    override fun buildChildSteps(): StepBuilder {
        return if (isGriefing) {
            StepBuilder.Companion.startWith(ReportTicketGriefingStep())
        } else if (isPlayer) {
            StepBuilder.Companion.startWith(ReportTicketPlayerStep())
        } else {
            StepBuilder.Companion.empty()
        }
    }

    private val isGriefing: Boolean
        get() = OPTION_GRIEFING == getSelected()

    private val isPlayer: Boolean
        get() = OPTION_PLAYER == getSelected()

    companion object {
        private const val OPTION_GRIEFING = "griefing"
        private const val OPTION_PLAYER = "player"

        private const val REPORTING_PLAYER_NAME_INPUT = "player-name"
    }
}
