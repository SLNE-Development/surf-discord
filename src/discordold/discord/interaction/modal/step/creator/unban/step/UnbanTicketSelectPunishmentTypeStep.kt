package dev.slne.discordold.discord.interaction.modal.step.creator.unban.step

import dev.minn.jda.ktx.emoji.toUnicodeEmoji
import dev.minn.jda.ktx.interactions.components.InlineModal
import dev.minn.jda.ktx.interactions.components.SelectOption
import dev.slne.discordold.discord.interaction.modal.step.ModalSelectionStep
import dev.slne.discordold.discord.interaction.modal.step.StepBuilder
import dev.slne.discordold.discord.interaction.modal.step.creator.unban.step.acban.UnbanTicketUploadModlistStep
import dev.slne.discordold.message.translatable
import dev.slne.discordold.persistence.service.punishment.PunishmentNoteService
import dev.slne.discordold.persistence.service.punishment.PunishmentService
import dev.slne.discordold.persistence.service.ticket.TicketService
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

private const val OPTION_YES = "yes"
private const val OPTION_NO = "no"

class UnbanTicketSelectPunishmentTypeStep(
    private val punishmentService: PunishmentService,
    private val ticketService: TicketService,
    private val punishmentNoteService: PunishmentNoteService
) :
    ModalSelectionStep(
        translatable("modal.unban.step.type.selection.title"),
        SelectOption(
            translatable("modal.unban.step.type.selection.yes.label"),
            OPTION_YES,
            description = translatable("modal.unban.step.type.selection.yes.description"),
            emoji = "✅".toUnicodeEmoji()
        ),
        SelectOption(
            translatable("modal.unban.step.type.selection.no.label"),
            OPTION_NO,
            description = translatable("modal.unban.step.type.selection.no.description"),
            emoji = "❌".toUnicodeEmoji()
        )
    ) {
    private val yes: Boolean
        get() = OPTION_YES == selected

    override fun InlineModal.buildModalComponents(
        interaction: StringSelectInteraction
    ) {
    }

    override fun buildChildSteps(): StepBuilder {
        val builder = StepBuilder.startWith(
            UnbanTicketPunishmentIdStep(
                punishmentService,
                ticketService,
                punishmentNoteService
            )
        )
            .then(::UnbanTicketUnbanAppealStep)

        if (yes) {
            builder.then { UnbanTicketUploadModlistStep() }
        }

        return builder
    }
}