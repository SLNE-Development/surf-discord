package dev.slne.surf.discord.ticket

import dev.slne.surf.discord.jda
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

typealias TicketData = ObjectSet<Pair<String, String>>

data class Ticket(
    val ticketUid: UUID,
    var ticketData: TicketData,
    val authorId: Long,
    val authorName: String,
    val authorAvatar: String?,
    val guildId: Long,
    val threadId: Long,
    val ticketType: TicketType,
    val createdAt: Long,
    var closedAt: Long?,
    var closedById: Long?,
    var closedByName: String?,
    var closedByAvatar: String?,
    var closedReason: String?,
) {
    fun getThreadChannel() = jda.getThreadChannelById(threadId)
    fun isClosed() = closedAt != null
}