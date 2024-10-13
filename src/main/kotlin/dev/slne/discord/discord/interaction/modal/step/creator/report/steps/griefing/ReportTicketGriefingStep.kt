package dev.slne.discord.discord.interaction.modal.step.creator.report.steps.griefing

import dev.minn.jda.ktx.interactions.components.InlineModal
import dev.minn.jda.ktx.interactions.components.SelectOption
import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.ModalSelectionStep
import dev.slne.discord.message.RawMessages.Companion.get
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent

private const val OPTION_SURVIVAL_01 = "survival-1"
private const val OPTION_SURVIVAL_02 = "survival-2"
private const val OPTION_EVENT = "event"

private const val WORLD_INPUT = "world"
private const val XYZ_INPUT = "x y z"
private const val WHAT_GRIEFED_INPUT = "what-griefed"
private const val ADDITIONAL_INFORMATION_INPUT = "additional-information"

class ReportTicketGriefingStep : ModalSelectionStep(
    get("modal.report.step.selection.griefing.title"),
    SelectOption(
        get("modal.report.step.selection.griefing.survival1.label"),
        OPTION_SURVIVAL_01,
        description = get("modal.report.step.selection.griefing.survival.description")
    ),
    SelectOption(
        get("modal.report.step.selection.griefing.survival2.label"),
        OPTION_SURVIVAL_02,
        description = get("modal.report.step.selection.griefing.survival.description")
    ),
    SelectOption(
        get("modal.report.step.selection.griefing.event.label"),
        OPTION_EVENT,
        description = get("modal.report.step.selection.griefing.event.description")
    )
) {
    private var world: String? = null
    private var xYZ: String? = null
    private var whatGriefed: String? = null
    private var additionalInformation: String? = null

    override fun InlineModal.buildModalComponents() {
        short(
            WORLD_INPUT,
            get("modal.report.step.griefing.input.world.label"),
            placeholder = get("modal.report.step.griefing.input.world.placeholder"),
            required = true
        )

        short(
            XYZ_INPUT,
            get("modal.report.step.griefing.input.xyz.label"),
            placeholder = get("modal.report.step.griefing.input.xyz.placeholder"),
            required = true
        ) { minLength = 5 }

        paragraph(
            WHAT_GRIEFED_INPUT,
            get("modal.report.step.griefing.input.what-griefed.label"),
            placeholder = get("modal.report.step.griefing.input.what-griefed.placeholder"),
            required = true
        )

        paragraph(
            ADDITIONAL_INFORMATION_INPUT,
            get("modal.report.step.griefing.input.additional-info.label"),
            placeholder = get("modal.report.step.griefing.input.additional-info.placeholder"),
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
            get("modal.report.step.griefing.messages.server", selectedServer)
        )
        addMessage(
            get("modal.report.step.griefing.messages.location", xYZ, world)
        )
        addEmptyLine()
        addMessage(
            "# " + get(
                "modal.report.step.griefing.input.what-griefed.label"
            )
        )
        addMessage("> $whatGriefed")

        additionalInformation?.let {
            addEmptyLine()
            addMessage(
                "## " + get(
                    "modal.report.step.griefing.input.additional-info.label"
                )
            )
            addMessage("> $it")
        }
    }

    private val selectedServer: String
        get() = when (selected) {
            OPTION_SURVIVAL_01 -> get("modal.report.step.selection.griefing.survival1.label")
            OPTION_SURVIVAL_02 -> get("modal.report.step.selection.griefing.survival2.label")
            OPTION_EVENT -> get("modal.report.step.selection.griefing.event.label")
            else -> throw IllegalStateException("Unexpected value: $selected")
        }
}
