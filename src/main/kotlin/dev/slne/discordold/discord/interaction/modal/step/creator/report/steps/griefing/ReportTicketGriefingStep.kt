package dev.slne.discordold.discord.interaction.modal.step.creator.report.steps.griefing

import dev.minn.jda.ktx.interactions.components.InlineModal
import dev.minn.jda.ktx.interactions.components.SelectOption
import dev.slne.discordold.discord.interaction.modal.step.MessageQueue
import dev.slne.discordold.discord.interaction.modal.step.ModalSelectionStep
import dev.slne.discordold.message.translatable
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

private const val OPTION_SURVIVAL_01 = "survival-1"
private const val OPTION_SURVIVAL_02 = "survival-2"
private const val OPTION_EVENT = "event"

private const val WORLD_INPUT = "world"
private const val XYZ_INPUT = "x y z"
private const val WHAT_GRIEFED_INPUT = "what-griefed"
private const val ADDITIONAL_INFORMATION_INPUT = "additional-information"

class ReportTicketGriefingStep : ModalSelectionStep(
    translatable("modal.report.step.selection.griefing.title"),
    SelectOption(
        translatable("modal.report.step.selection.griefing.survival1.label"),
        OPTION_SURVIVAL_01,
        description = translatable("modal.report.step.selection.griefing.survival.description")
    ),
    SelectOption(
        translatable("modal.report.step.selection.griefing.survival2.label"),
        OPTION_SURVIVAL_02,
        description = translatable("modal.report.step.selection.griefing.survival.description")
    ),
    SelectOption(
        translatable("modal.report.step.selection.griefing.event.label"),
        OPTION_EVENT,
        description = translatable("modal.report.step.selection.griefing.event.description")
    )
) {
    private var world: String? = null
    private var xYZ: String? = null
    private var whatGriefed: String? = null
    private var additionalInformation: String? = null

    override fun InlineModal.buildModalComponents(interaction: StringSelectInteraction) {
        short(
            WORLD_INPUT,
            translatable("modal.report.step.griefing.input.world.label"),
            placeholder = translatable("modal.report.step.griefing.input.world.placeholder"),
            required = true
        )

        short(
            XYZ_INPUT,
            translatable("modal.report.step.griefing.input.xyz.label"),
            placeholder = translatable("modal.report.step.griefing.input.xyz.placeholder"),
            required = true
        ) { minLength = 5 }

        paragraph(
            WHAT_GRIEFED_INPUT,
            translatable("modal.report.step.griefing.input.what-griefed.label"),
            placeholder = translatable("modal.report.step.griefing.input.what-griefed.placeholder"),
            required = true
        )

        paragraph(
            ADDITIONAL_INFORMATION_INPUT,
            translatable("modal.report.step.griefing.input.additional-info.label"),
            placeholder = translatable("modal.report.step.griefing.input.additional-info.placeholder"),
            required = false
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
