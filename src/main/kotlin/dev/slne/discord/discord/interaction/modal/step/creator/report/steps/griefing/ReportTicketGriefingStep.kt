package dev.slne.discord.discord.interaction.modal.step.creator.report.steps.griefing

import dev.slne.discord.discord.interaction.modal.step.*
import dev.slne.discord.message.RawMessages.Companion.get
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle

class ReportTicketGriefingStep : ModalSelectionStep(
    get("modal.report.step.selection.griefing.title"),
    SelectOption.of(
        get("modal.report.step.selection.griefing.survival.label")!!,
        OPTION_SURVIVAL
    )
        .withDescription(
            get("modal.report.step.selection.griefing.survival.description")
        ),
    SelectOption.of(
        get("modal.report.step.selection.griefing.event.label")!!,
        OPTION_EVENT
    )
        .withDescription(
            get("modal.report.step.selection.griefing.event.description")
        )
) {
    private var world: String? = null
    private var xYZ: String? = null
    private var whatGriefed: String? = null
    private var additionalInformation: String? = null

    override fun buildModalComponents(builder: ModalComponentBuilder) {
        builder.addComponent(
            TextInput.create(
                WORLD_INPUT,
                get("modal.report.step.griefing.input.world.label")!!,
                TextInputStyle.SHORT
            )
                .setRequired(true)
                .setPlaceholder(get("modal.report.step.griefing.input.world.placeholder"))
                .build()
        )
        builder.addComponent(
            TextInput.create(
                XYZ_INPUT,
                get("modal.report.step.griefing.input.xyz.label")!!,
                TextInputStyle.SHORT
            )
                .setRequired(true)
                .setMinLength(5)
                .setPlaceholder(get("modal.report.step.griefing.input.xyz.placeholder"))
                .build()
        )
        builder.addComponent(
            TextInput.create(
                WHAT_GRIEFED_INPUT,
                get("modal.report.step.griefing.input.what-griefed.label")!!,
                TextInputStyle.PARAGRAPH
            )
                .setRequired(true)
                .setPlaceholder(
                    get(
                        "modal.report.step.griefing.input.what-griefed.placeholder"
                    )
                )
                .build()
        )
        builder.addComponent(
            TextInput.create(
                ADDITIONAL_INFORMATION_INPUT,
                get("modal.report.step.griefing.input.additional-info.label")!!,
                TextInputStyle.PARAGRAPH
            )
                .setRequired(false)
                .setPlaceholder(
                    get(
                        "modal.report.step.griefing.input.additional-info.placeholder"
                    )
                )
                .build()
        )
    }

    @Throws(ModalStepInputVerificationException::class)
    override fun verifyModalInput(event: ModalInteractionEvent) {
        world = getRequiredInput(event, WORLD_INPUT)
        xYZ = getRequiredInput(event, XYZ_INPUT)
        whatGriefed = getRequiredInput(event, WHAT_GRIEFED_INPUT)
        additionalInformation = getOptionalInput(event, ADDITIONAL_INFORMATION_INPUT)
    }

    override fun buildOpenMessages(messages: MessageQueue, channel: TextChannel?) {
        messages.addMessage(
            get("modal.report.step.griefing.messages.server", selectedServer)!!
        )
        messages.addMessage(
            get("modal.report.step.griefing.messages.location", xYZ, world)!!
        )
        messages.addEmptyLine()
        messages.addMessage(
            "# " + get(
                "modal.report.step.griefing.input.what-griefed.label"
            )
        )
        messages.addMessage("> $whatGriefed")

        if (additionalInformation != null && !additionalInformation!!.isBlank()) {
            messages.addEmptyLine()
            messages.addMessage(
                "## " + get(
                    "modal.report.step.griefing.input.additional-info.label"
                )
            )
            messages.addMessage("> $additionalInformation")
        }
    }

    private val selectedServer: String
        get() = when (getSelected()) {
            OPTION_SURVIVAL -> get("modal.report.step.selection.griefing.survival.label")!!
            OPTION_EVENT -> get("modal.report.step.selection.griefing.event.label")!!
            else -> throw IllegalStateException("Unexpected value: " + getSelected())
        }

    companion object {
        private const val OPTION_SURVIVAL = "survival"
        private const val OPTION_EVENT = "event"

        private const val WORLD_INPUT = "world"
        private const val XYZ_INPUT = "x y z"
        private const val WHAT_GRIEFED_INPUT = "what-griefed"
        private const val ADDITIONAL_INFORMATION_INPUT = "additional-information"
    }
}
