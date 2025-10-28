package dev.slne.surf.discord.ticket.button

import dev.slne.surf.discord.ticket.TicketType
import jakarta.annotation.PostConstruct
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component

@Component
class TicketSelectMenuListener(private val jda: JDA) : ListenerAdapter() {

    @PostConstruct
    fun init() {
        jda.addEventListener(this)
    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        if (event.componentId != "surf-discord-ticket-reason") return

        val selected = event.selectedOptions.firstOrNull()
        if (selected == null) {
            event.deferReply(true).queue {
                it.editOriginal("Ein Fehler ist aufgetreten. Bitte versuche es erneut.").queue()
            }
            return
        }

        event.deferReply(true).queue {
            val ticketType = getTicketType(selected.label) ?: return@queue
            val modal = ticketType.modal

            if (modal != null) {
                event.replyModal(modal)
            } else {
                it.editOriginal("TODO: Ticket Modal: ${selected.label}").queue()
            }
        }
    }

    private fun getTicketType(label: String) = TicketType.entries.find { it.displayName == label }
}
