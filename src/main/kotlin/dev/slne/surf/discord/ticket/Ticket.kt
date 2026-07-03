package dev.slne.surf.discord.ticket

import dev.minn.jda.ktx.coroutines.await
import dev.slne.surf.discord.config.botConfig
import dev.slne.surf.discord.jda
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
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

    suspend fun retrieveThreadChannel(): ThreadChannel? = withContext(Dispatchers.IO) {
        val tid = threadId ?: return@withContext null

        val cached = getThreadChannel()
        if (cached != null) return@withContext cached

        val ticketChannel = jda.getTextChannelById(botConfig.channels.ticketChannel)
            ?: return@withContext null

        val threads = ticketChannel.retrieveArchivedPrivateThreadChannels()
            .limit(100)
            .await()

        return@withContext threads.firstOrNull { it.idLong == tid }

    fun isClosed() = closedAt != null
}