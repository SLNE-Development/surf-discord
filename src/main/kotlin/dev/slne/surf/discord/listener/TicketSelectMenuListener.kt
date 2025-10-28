package dev.slne.surf.discord.listener

import dev.slne.surf.discord.command.dsl.modal
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.ticket.TicketType
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import org.springframework.stereotype.Component

@Component
class TicketSelectMenuListener(
    private val jda: JDA,
    private val ticketService: TicketService,
    private val scope: CoroutineScope
) : ListenerAdapter() {

    @PostConstruct
    fun init() {
        jda.addEventListener(this)
    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        when (event.componentId) {
            "ticket-type" -> handleTicketReasonSelection(event)
            "close-ticket-reason" -> handleTicketReason(event)
        }
    }

    private fun handleTicketReason(event: StringSelectInteractionEvent) = scope.launch {
        val selected = event.selectedOptions.firstOrNull()
        if (selected == null) {
            event.hook.editOriginal("Ein Fehler ist aufgetreten. Bitte versuche es erneut.").queue()
            return@launch
        }

        val ticket = ticketService.getTicketByThreadId(event.channel.idLong) ?: run {
            event.hook.editOriginal("Ein Fehler ist aufgetreten.").queue()
            return@launch
        }

        if (selected.description == "custom") {
            event.replyModal(
                modal("custom-close-reason", "Ticket mit benutzerdefiniertem Grund schließen") {
                    field {
                        id = "custom-close-reason-content"
                        label = "Grund für das Schließen des Tickets"
                        style = TextInputStyle.PARAGRAPH
                        placeholder = "Kein Grund angegeben..."
                    }
                }
            )
        } else {
            ticketService.closeTicket(
                ticket,
                selected.description ?: "Kein Grund angegeben.",
                event.user
            )

            event.reply("Das Ticket wird geschlossen...").setEphemeral(true).queue()
        }
    }

    private fun handleTicketReasonSelection(event: StringSelectInteractionEvent) = scope.launch {
        val selected = event.selectedOptions.firstOrNull()
        if (selected == null) {
            event.hook.editOriginal("Ein Fehler ist aufgetreten. Bitte versuche es erneut.").queue()
            return@launch
        }

        val ticketType = getTicketType(selected.label) ?: run {
            event.hook.editOriginal("Ein Fehler ist aufgetreten.")
            return@launch
        }

        if (ticketService.hasTicket(event.user.idLong, ticketType)) {
            event.hook.editOriginal("Du hast bereits ein offenes Ticket dieses Typs.").queue()
            return@launch
        }

        val modal = ticketType.modal

        if (modal != null) {
            event.hook.deleteOriginal().queue()
            event.replyModal(modal).queue()
        } else {
            event.hook.editOriginal("TODO: Ticket Modal: ${selected.label}").queue()
        }
    }

    private fun getTicketType(label: String) = TicketType.entries.find { it.displayName == label }
}
