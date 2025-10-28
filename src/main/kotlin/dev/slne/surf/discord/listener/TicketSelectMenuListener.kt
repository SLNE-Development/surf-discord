package dev.slne.surf.discord.listener

import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.ticket.TicketType
import jakarta.annotation.PostConstruct
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component

@Component
class TicketSelectMenuListener(
    private val jda: JDA,
    private val ticketService: TicketService
) : ListenerAdapter() {

    @PostConstruct
    fun init() {
        jda.addEventListener(this)
    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        if (event.componentId != "ticket-reason") {
            return
        }

        val selected = event.selectedOptions.firstOrNull()
        if (selected == null) {
            event.hook.editOriginal("Ein Fehler ist aufgetreten. Bitte versuche es erneut.").queue()
            return
        }

        val ticketType = getTicketType(selected.label) ?: run {
            event.hook.editOriginal("Ein Fehler ist aufgetreten.")
            return
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
