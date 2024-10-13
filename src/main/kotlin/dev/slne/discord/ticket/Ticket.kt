package dev.slne.discord.ticket

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.DiscordBot
import dev.slne.discord.guild.getDiscordGuildByGuildId
import dev.slne.discord.message.Messages
import dev.slne.discord.ticket.message.TicketMessage
import dev.slne.discord.ticket.result.TicketCreateResult
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import java.time.ZonedDateTime
import java.util.*

class Ticket(
    guild: Guild?,
    ticketAuthor: User?,
    ticketType: TicketType?
) {
    constructor() : this(null, null, null)

    var id: Long = -1

    var ticketId: UUID? = null

    var openedAt: ZonedDateTime = ZonedDateTime.now()

    private var guildId: String? = guild?.id

    var threadId: String? = null

    private var ticketTypeString: String? = ticketType?.name

    var ticketAuthorId: String? = ticketAuthor?.id

    var ticketAuthorName: String? = ticketAuthor?.name

    var ticketAuthorAvatarUrl: String? = ticketAuthor?.avatarUrl

    var closedById: String? = null

    var closedReason: String? = null

    var closedByAvatarUrl: String? = null

    var closedByName: String? = null

    var closedAt: ZonedDateTime? = null

    private val _messages = mutableListOf<TicketMessage>()
    val messages get() = _messages.toList()

    var createdAt: ZonedDateTime? = null

    fun addRawTicketMessage(ticketMessage: TicketMessage) = _messages.add(ticketMessage)

    fun addTicketRole(role: Role) {
        TODO("To be implemented when ticket channels are removed in favor for threads")
//        val guildConfig = getGuildConfigByGuildId(guildId) ?: return
//        val roleConfig = getRoleConfig(guildId, role.name) ?: return

    }

    fun getTicketMessage(message: Message) = _messages.find { it.messageId.equals(message.id) }

    fun getTicketMessage(messageId: String?) =
        _messages.firstOrNull { it.messageId.equals(messageId) }

    val ticketAuthor
        get() = ticketAuthorId?.let { DiscordBot.jda.retrieveUserById(it) }

    val ticketType
        get() = TicketType.valueOf(ticketTypeString!!)

    val guild
        get() = guildId?.let { DiscordBot.jda.getGuildById(it) }

    val thread
        get() = threadId?.let { DiscordBot.jda.getThreadChannelById(it) }

    val closedBy
        get() = closedById?.let { DiscordBot.jda.retrieveUserById(it) }

    fun updateFrom(other: Ticket) {
        openedAt = other.openedAt
        ticketId = other.ticketId
        id = other.id
        createdAt = other.createdAt
        closedAt = other.closedAt
    }

    fun hasTicketId() = ticketId != null

    fun hasGuild() = guildId != null

    val closeReasonOrDefault: String
        get() = closedReason ?: Messages.DEFAULT_TICKET_CLOSED_REASON

    suspend fun openFromButton(): TicketCreateResult {
        val s = getDiscordGuildByGuildId(guildId!!)!!.discordGuild.ticketChannels[ticketType]!!
        val textChannelById = guild?.getTextChannelById(s)!!

        TicketChannelHelper.createThread(
            this,
            TicketChannelHelper.generateTicketName(ticketType, ticketAuthor!!.await()),
            textChannelById
        )

        return TicketCreateResult.SUCCESS
    }

    suspend fun addTicketMessage(fromTicketAndMessage: TicketMessage): TicketMessage =
        TODO("Implement")

    suspend fun save(): Ticket = this // TODO: Implement
}
