package dev.slne.surf.discord.ticket

import dev.slne.surf.discord.jda
import it.unimi.dsi.fastutil.objects.ObjectSet

typealias TicketData = ObjectSet<Pair<String, String>>

data class Ticket(
    val ticketId: Long,
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

    fun isClosed(): Boolean {
        return closedAt != null
    }
}