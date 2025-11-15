package dev.slne.discord.discord.interaction.modal.step.creator.unban.step

import dev.slne.discord.discord.interaction.modal.step.ModalSelectionStep
import dev.slne.discord.discord.interaction.modal.step.StepBuilder
import dev.slne.discord.discord.interaction.modal.step.creator.unban.step.acban.UnbanTicketUploadModlistStep
import dev.slne.discord.message.translatable
import dev.slne.discord.persistence.service.punishment.PunishmentNoteService
import dev.slne.discord.persistence.service.punishment.PunishmentService
import dev.slne.discord.persistence.service.ticket.TicketService
import net.dv8tion.jda.api.components.selections.SelectOption
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction
import net.dv8tion.jda.api.modals.Modal

private const val OPTION_YES = "yes"
private const val OPTION_NO = "no"

class UnbanTicketSelectPunishmentTypeStep(
    private val punishmentService: PunishmentService,
    private val ticketService: TicketService,
    private val punishmentNoteService: PunishmentNoteService
) : ModalSelectionStep(
    translatable("modal.unban.step.type.selection.title"),
    SelectOption.of(
        translatable("modal.unban.step.type.selection.yes.label"),
        OPTION_YES
    ).withDescription(translatable("modal.unban.step.type.selection.yes.description"))
        .withEmoji(Emoji.fromUnicode("✅")),
    SelectOption.of(
        translatable("modal.unban.step.type.selection.no.label"),
        OPTION_NO
    ).withDescription(translatable("modal.unban.step.type.selection.no.description"))
        .withEmoji(Emoji.fromUnicode("❌"))
) {
    private val yes: Boolean
        get() = OPTION_YES == selected

    override fun buildModalComponents(builder: Modal.Builder, interaction: StringSelectInteraction) {
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