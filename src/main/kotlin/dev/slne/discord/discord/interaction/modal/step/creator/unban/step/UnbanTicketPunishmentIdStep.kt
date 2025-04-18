package dev.slne.discord.discord.interaction.modal.step.creator.unban.step

import dev.minn.jda.ktx.interactions.components.InlineModal
import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.ModalStep
import dev.slne.discord.message.translatable
import dev.slne.discord.persistence.external.PunishmentNote
import dev.slne.discord.persistence.service.punishment.PunishmentNoteService
import dev.slne.discord.persistence.service.punishment.PunishmentService
import dev.slne.discord.persistence.service.ticket.TicketService
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

private const val PUNISHMENT_ID = "punishment-id"

class UnbanTicketPunishmentIdStep(
    private val punishmentService: PunishmentService,
    private val ticketService: TicketService,
    private val punishmentNoteService: PunishmentNoteService
) : ModalStep() {

    private var punishmentId: String? = null

    override fun InlineModal.buildModalComponents(interaction: StringSelectInteraction) {
        short(
            PUNISHMENT_ID,
            translatable("modal.unban.step.punishment-id.input.label"),
            placeholder = translatable("modal.unban.step.punishment-id.input.placeholder"),
            required = true,
            requiredLength = 6..8
        )
    }

    override suspend fun verifyModalInput(event: ModalInteractionEvent) {
        punishmentId = getInput(event, PUNISHMENT_ID)

        if (!punishmentService.isValidPunishmentId(punishmentId!!)) {
            throw ModalStepInputVerificationException(
                translatable("modal.unban.step.punishment-id.error.invalid")
            )
        }
    }

    override suspend fun onPostThreadCreated(thread: ThreadChannel) {
        super.onPostThreadCreated(thread)

        if (punishmentId == null) {
            error("PunishmentId is null after thread creation, should not happen")
        }

        val punishment = punishmentService.getPunishmentBanByPunishmentId(punishmentId!!)
            ?: error("Punishment not found for punishemntId: $punishmentId")

        val ticket = ticketService.getTicketByThreadId(thread.id)
            ?: error("Ticket not found for punishmentId $punishmentId")

        val punishmentDbId = punishment.id
            ?: error("Punishment id is null for punishmentId: $punishmentId")

        val noteIde = punishmentNoteService.generateNoteId()
        punishmentNoteService.createPunishmentNote(
            PunishmentNote(
                noteId = noteIde,
                notableId = punishmentDbId,
                notableType = "App\\Models\\Punishment\\BanPunishment",
                note = "UNBAN: https://admin.slne.dev/ticket/${ticket.ticketId}",
                generated = true,
                unbanTicketOpened = ticket.openedAt
            )
        )
    }

    override fun MessageQueue.buildOpenMessages(thread: ThreadChannel) {
        addMessage(
            translatable(
                "modal.unban.step.punishment-id.messages.punishment-id",
                punishmentId
            )
        )
    }
}
