package dev.slne.surf.discord.ticket

import dev.slne.surf.discord.jda
import java.time.ZonedDateTime
import java.util.*

typealias TicketData = Map<String, String>

data class Ticket(
    val ticketId: UUID,
    var ticketData: TicketData,
    val authorId: Long,
    val authorName: String,
    val authorAvatar: String?,
    val guildId: Long,
    val threadId: Long?,
    val ticketType: TicketType,
    val createdAt: ZonedDateTime,
    var closedAt: ZonedDateTime?,
    var closedById: Long?,
    var closedByName: String?,
    var closedByAvatar: String?,
    var closedReason: String?,
) {
    var internalTicketId: ULong? = null

    fun getThreadChannel() = threadId?.let { jda.getThreadChannelById(it) }
    fun isClosed() = closedAt != null
}