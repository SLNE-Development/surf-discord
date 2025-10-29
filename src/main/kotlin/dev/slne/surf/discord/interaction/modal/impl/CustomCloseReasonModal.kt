package dev.slne.surf.discord.interaction.modal.impl

import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.interaction.modal.DiscordModal
import dev.slne.surf.discord.ticket.TicketService
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import org.springframework.stereotype.Component

@Component
class CustomCloseReasonModal(
    private val ticketService: TicketService
) : DiscordModal {
    override val id = "ticket:close:reason:custom"
    override fun create() = modal(id, "Ticket mit eigenem Grund schließen") {
        field {
            id = "custom-close-reason-content"
            label = "Grund für das Schließen des Tickets"
            style = TextInputStyle.PARAGRAPH
            placeholder = "Kein Grund angegeben..."
        }
    }

    override suspend fun create(hook: InteractionHook) = error("Unsupported")


    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val interaction = event.interaction
        val customReason = interaction.getValue("custom-close-reason-content")?.asString ?: return

        ticketService.closeTicket(interaction.hook, customReason)
        interaction.hook.deleteOriginal()
        interaction.reply("Das Ticket wird geschlossen...").setEphemeral(true).queue()
    }
}