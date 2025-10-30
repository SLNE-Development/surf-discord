package dev.slne.surf.discord.logging

import dev.slne.surf.discord.config.botConfig
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.ticket.Ticket
import dev.slne.surf.discord.util.Colors
import org.springframework.stereotype.Service

@Service
class TicketLogger {
    fun logCreation(ticket: Ticket) {
        val channel =
            ticket.getThreadChannel()?.guild?.getTextChannelById(botConfig.channels.ticketLogChannel)
                ?: return

        channel.sendMessageEmbeds(embed {
            title = "Ticket erstellt"
            description = "Ein neues Ticket wurde erstellt."
            color = Colors.SUCCESS

            field {
                name = "Ticket ID"
                value = ticket.ticketId.toString()
                inline = true
            }

            field {
                name = "Ticket Typ"
                value = ticket.ticketType.displayName
                inline = true
            }

            field {
                name = "Erstellt von"
                value = "${ticket.authorName} (${ticket.authorId})"
                inline = true
            }

            footer = "Arty Support | 2025"
        }).queue()
    }

    fun logNewWatcher(ticket: Ticket, watcherName: String) {
        val channel =
            ticket.getThreadChannel()?.guild?.getTextChannelById(botConfig.channels.ticketLogChannel)
                ?: return

        channel.sendMessageEmbeds(embed {
            title = "Ticket Status Update"
            description = "Ein neues Teammitglied wurde als Beobachter zu einem Ticket hinzugefügt."

            field {
                name = "Ticket ID"
                value = ticket.ticketId.toString()
                inline = true
            }

            field {
                name = "Ticket Typ"
                value = ticket.ticketType.displayName
                inline = true
            }

            field {
                name = "Beobachter"
                value = watcherName
                inline = true
            }
        }).queue()
    }

    fun logNewClaimant(ticket: Ticket, claimantName: String) {
        val channel =
            ticket.getThreadChannel()?.guild?.getTextChannelById(botConfig.channels.ticketLogChannel)
                ?: return

        channel.sendMessageEmbeds(embed {
            title = "Ticket Status Update"
            description = "Ein Teammitglied hat ein Ticket übernommen."

            field {
                name = "Ticket ID"
                value = ticket.ticketId.toString()
                inline = true
            }

            field {
                name = "Ticket Typ"
                value = ticket.ticketType.displayName
                inline = true
            }

            field {
                name = "Übernommen von"
                value = claimantName
                inline = true
            }
        }).queue()
    }

    fun logNewUnWatcher(ticket: Ticket, unwatcherName: String) {
        val channel =
            ticket.getThreadChannel()?.guild?.getTextChannelById(botConfig.channels.ticketLogChannel)
                ?: return

        channel.sendMessageEmbeds(embed {
            title = "Ticket Status Update"
            description = "Ein Teammitglied beobachtet ein Ticket nicht mehr."

            field {
                name = "Ticket ID"
                value = ticket.ticketId.toString()
                inline = true
            }

            field {
                name = "Ticket Typ"
                value = ticket.ticketType.displayName
                inline = true
            }

            field {
                name = "Teammitglied"
                value = unwatcherName
                inline = true
            }
        }).queue()
    }

    fun logNewUnClaimant(ticket: Ticket, unclaimantName: String) {
        val channel =
            ticket.getThreadChannel()?.guild?.getTextChannelById(botConfig.channels.ticketLogChannel)
                ?: return

        channel.sendMessageEmbeds(embed {
            title = "Ticket Status Update"
            description = "Ein Teammitglied hat ein Ticket abgegeben."

            field {
                name = "Ticket ID"
                value = ticket.ticketId.toString()
                inline = true
            }

            field {
                name = "Ticket Typ"
                value = ticket.ticketType.displayName
                inline = true
            }

            field {
                name = "Abgegeben von"
                value = unclaimantName
                inline = true
            }
        }).queue()
    }

    fun logClosure(ticket: Ticket) {
        val channel =
            ticket.getThreadChannel()?.guild?.getTextChannelById(botConfig.channels.ticketLogChannel)
                ?: return

        channel.sendMessageEmbeds(embed {
            title = "Ticket geschlossen"
            description = "Ein Ticket wurde geschlossen."
            color = Colors.ERROR

            field {
                name = "Ticket ID"
                value = ticket.ticketId.toString()
                inline = true
            }

            field {
                name = "Ticket Typ"
                value = ticket.ticketType.displayName
                inline = true
            }

            field {
                name = "Erstellt von"
                value = "${ticket.authorName} (${ticket.authorId})"
                inline = true
            }

            field {
                name = "Zeitraum"
                value =
                    "<t:${ticket.createdAt / 1000}:F> - <t:${System.currentTimeMillis() / 1000}:F>"
                inline = true
            }

            field {
                name = "Geschlossen von"
                value = ticket.closedByName ?: "Unbekannt"
                inline = true
            }

            field {
                name = "Schließungsgrund"
                value = ticket.closedReason ?: "Kein Grund angegeben"
                inline = true
            }

            footer = "Arty Support | 2025"
        }).queue()
    }
}