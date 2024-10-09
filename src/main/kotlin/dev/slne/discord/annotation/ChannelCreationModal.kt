package dev.slne.discord.annotation

import dev.slne.discord.ticket.TicketType

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ChannelCreationModal(val modalId: String = "", val ticketType: TicketType)
