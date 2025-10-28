package dev.slne.surf.discord.listener

import dev.slne.surf.discord.command.dsl.embed
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.ticket.TicketType
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import org.springframework.stereotype.Component
import java.awt.Color

@Component
class TicketButtonListener(
    private val jda: JDA,
    private val ticketService: TicketService,
    private val discordScope: CoroutineScope
) : ListenerAdapter() {

    @PostConstruct
    fun init() {
        jda.addEventListener(this)
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        when (event.componentId) {
            "open-ticket" -> handleOpenTicket(event)
            "close-ticket" -> handleCloseTicket(event)
            "confirm-close-ticket" -> handleConfirmCloseTicket(event)
        }
    }

    private fun handleConfirmCloseTicket(event: ButtonInteractionEvent) = discordScope.launch(
        Dispatchers.IO
    ) {
        ticketService.closeTicket(event.hook, "Das Ticket wurde geschlossen.")
    }

    private fun handleCloseTicket(event: ButtonInteractionEvent) = discordScope.launch {
        val ticket = ticketService.getTicketByThreadId(event.channel.idLong) ?: return@launch
        event.deferReply(true).queue {
            val builder = StringSelectMenu.create("close-ticket-reason")

            builder.addOption("Eigener Grund", "custom")

            ticket.ticketType.closeReasons.forEach { rsn ->
                builder.addOption(rsn.displayName, rsn.description)
            }

            it.editOriginal("Wähle den Grund...").setActionRow(builder.build()).queue()
        }
    }

    private fun handleOpenTicket(event: ButtonInteractionEvent) {
        val builder = StringSelectMenu.create("ticket-type")

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
