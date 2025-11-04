package dev.slne.surf.discord.ticket

import dev.slne.surf.discord.jda
import java.util.*

typealias TicketData = Map<String, String>

data class Ticket(
    val ticketUid: UUID,
    var ticketData: TicketData,
    val authorId: Long,
    val authorName: String,
    val authorAvatar: String?,
    val guildId: Long,
    val threadId: Long?,
    val ticketType: TicketType,
    val createdAt: Long,
    var closedAt: Long?,
    var closedById: Long?,
    var closedByName: String?,
    var closedByAvatar: String?,
    var closedReason: String?,
) {
    fun getThreadChannel() = threadId?.let { jda.getThreadChannelById(it) }
    fun isClosed() = closedAt != null
}