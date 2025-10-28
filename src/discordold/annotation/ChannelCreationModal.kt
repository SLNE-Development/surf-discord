package dev.slne.discordold.annotation

import dev.slne.discordold.ticket.TicketType

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ChannelCreationModal(val modalId: String = "", val ticketType: TicketType)
