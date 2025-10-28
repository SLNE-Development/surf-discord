package dev.slne.surf.discord.ticket.button

import dev.slne.surf.discord.command.dsl.embed
import dev.slne.surf.discord.ticket.TicketType
import jakarta.annotation.PostConstruct
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import org.springframework.stereotype.Component
import java.awt.Color

@Component
class TicketButtonListener(private val jda: JDA) : ListenerAdapter() {

    @PostConstruct
    fun init() {
        jda.addEventListener(this)
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        if (event.componentId != "surf-discord-open-ticket") return

        val builder = StringSelectMenu.create("surf-discord-ticket-reason")

        TicketType.entries.forEach {
            builder.addOption(it.displayName, it.description)
        }

        builder.setPlaceholder("Ticket Typ wählen...")
        builder.setRequiredRange(1, 1)

        event.deferReply(true).queue {
            it.sendMessageEmbeds(
                embed {
                    title = "Ticket Typ wählen"
                    description = """
                        Bitte wähle das passende Ticket aus, welches du öffnen möchtest.

                        Informationen zu den unterschiedlichen Tickettypen findest du auf https://server.castcrafter.de/support
                    """.trimIndent()
                    color = Color(31, 189, 210)
                }
            ).addActionRow(builder.build()).setEphemeral(true).queue()
        }
    }
}
