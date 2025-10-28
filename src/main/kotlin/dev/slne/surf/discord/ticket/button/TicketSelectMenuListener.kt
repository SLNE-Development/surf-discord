package dev.slne.surf.discord.ticket.button

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
            it.editOriginal("TODO: Ticket Modal: ${selected.label}").queue()
        }
    }
}
