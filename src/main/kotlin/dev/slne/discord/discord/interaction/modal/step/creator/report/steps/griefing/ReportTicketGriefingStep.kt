package dev.slne.discord.discord.interaction.modal.step.creator.report.steps.griefing

import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.ModalSelectionStep
import dev.slne.discord.message.translatable
import net.dv8tion.jda.api.components.label.Label
import net.dv8tion.jda.api.components.selections.SelectOption
import net.dv8tion.jda.api.components.textinput.TextInput
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction
import net.dv8tion.jda.api.modals.Modal

private const val OPTION_SURVIVAL_01 = "survival-1"
private const val OPTION_SURVIVAL_02 = "survival-2"
private const val OPTION_EVENT = "event"

private const val WORLD_INPUT = "world"
private const val XYZ_INPUT = "x y z"
private const val WHAT_GRIEFED_INPUT = "what-griefed"
private const val ADDITIONAL_INFORMATION_INPUT = "additional-information"

class ReportTicketGriefingStep : ModalSelectionStep(
    translatable("modal.report.step.selection.griefing.title"),
    SelectOption.of(
        translatable("modal.report.step.selection.griefing.survival1.label"),
        OPTION_SURVIVAL_01
    ).withDescription(translatable("modal.report.step.selection.griefing.survival.description")),
    SelectOption.of(
        translatable("modal.report.step.selection.griefing.survival2.label"),
        OPTION_SURVIVAL_02
    ).withDescription(translatable("modal.report.step.selection.griefing.survival.description")),
    SelectOption.of(
        translatable("modal.report.step.selection.griefing.event.label"),
        OPTION_EVENT
    ).withDescription(translatable("modal.report.step.selection.griefing.event.description"))
) {
    private var world: String? = null
    private var xYZ: String? = null
    private var whatGriefed: String? = null
    private var additionalInformation: String? = null

    override fun buildModalComponents(builder: Modal.Builder, interaction: StringSelectInteraction) {
        builder.addComponents(
            Label.of(
                translatable("modal.report.step.griefing.input.world.label"),
                TextInput.create(WORLD_INPUT, TextInputStyle.SHORT)
                    .setPlaceholder(translatable("modal.report.step.griefing.input.world.placeholder"))
                    .setRequired(true)
                    .build()
            ),
            Label.of(
                translatable("modal.report.step.griefing.input.xyz.label"),
                TextInput.create(XYZ_INPUT, TextInputStyle.SHORT)
                    .setPlaceholder(translatable("modal.report.step.griefing.input.xyz.placeholder"))
                    .setRequired(true)
                    .setMinLength(5)
                    .build()
            ),
            Label.of(
                translatable("modal.report.step.griefing.input.what-griefed.label"),
                TextInput.create(WHAT_GRIEFED_INPUT, TextInputStyle.PARAGRAPH)
                    .setPlaceholder(translatable("modal.report.step.griefing.input.what-griefed.placeholder"))
                    .setRequired(true)
                    .build()
            ),
            Label.of(
                translatable("modal.report.step.griefing.input.additional-info.label"),
                TextInput.create(ADDITIONAL_INFORMATION_INPUT, TextInputStyle.PARAGRAPH)
                    .setPlaceholder(translatable("modal.report.step.griefing.input.additional-info.placeholder"))
                    .setRequired(false)
                    .build()
            )
        )
    }

    override suspend fun verifyModalInput(event: ModalInteractionEvent) {
        world = event[WORLD_INPUT]
        xYZ = event[XYZ_INPUT]
        whatGriefed = event[WHAT_GRIEFED_INPUT]
        additionalInformation = getOptionalInput(event, ADDITIONAL_INFORMATION_INPUT)
    }

    override fun MessageQueue.buildOpenMessages(thread: ThreadChannel) {
        addMessage(
            translatable("modal.report.step.griefing.messages.server", selectedServer)
        )
        addMessage(
            translatable("modal.report.step.griefing.messages.location", xYZ, world)
        )
        addEmptyLine()
        addMessage(
            "# " + translatable(
                "modal.report.step.griefing.input.what-griefed.label"
            )
        )
        addMessage("> $whatGriefed")

        if (!additionalInformation.isNullOrEmpty()) {
            addEmptyLine()
            addMessage(
                "## " + translatable(
                    "modal.report.step.griefing.input.additional-info.label"
                )
            )
            addMessage("> $additionalInformation")
        }
    }

    private val selectedServer: String
        get() = when (selected) {
            OPTION_SURVIVAL_01 -> translatable("modal.report.step.selection.griefing.survival1.label")
            OPTION_SURVIVAL_02 -> translatable("modal.report.step.selection.griefing.survival2.label")
            OPTION_EVENT -> translatable("modal.report.step.selection.griefing.event.label")
            else -> throw IllegalStateException("Unexpected value: $selected")
        }
}
