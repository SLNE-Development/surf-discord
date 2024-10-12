package dev.slne.discord.ticket

import dev.slne.discord.DiscordBot
import dev.slne.discord.message.Messages
import dev.slne.discord.ticket.member.TicketMember
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

    private val removedMembers: MutableList<TicketMember> = ArrayList<TicketMember>()

    var id: Long = -1

    var ticketId: UUID? = null

    var openedAt: ZonedDateTime = ZonedDateTime.now()

    var guildId: String? = guild?.id

    var threadId: String? = null

    var ticketTypeString: String? = ticketType?.name

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

    private val _members = mutableListOf<TicketMember>()
    val members get() = _members.toList()

    var createdAt: ZonedDateTime? = null

    fun addRawTicketMember(ticketMember: TicketMember) = _members.add(ticketMember)

    fun removeRawTicketMember(ticketMember: TicketMember) {
        _members.remove(ticketMember)
        removedMembers.add(ticketMember)
    }

    fun addRawTicketMessage(ticketMessage: TicketMessage) = _messages.add(ticketMessage)

    fun memberExists(user: User) = _members.any { it.memberId == user.id && !it.isRemoved }

    fun addTicketRole(role: Role) {
        TODO("To be implemented when ticket channels are removed in favor for threads")
//        val guildConfig = getGuildConfigByGuildId(guildId) ?: return
//        val roleConfig = getRoleConfig(guildId, role.name) ?: return

    }

    fun getTicketMessage(message: Message) = _messages.find { it.messageId.equals(message.id) }

    fun getTicketMessage(messageId: String?) =
        _messages.firstOrNull { it.messageId.equals(messageId) }

    fun getTicketMember(user: User) = _members.firstOrNull { it.memberId.equals(user.id) }

    fun getActiveTicketMember(user: User) =
        _members.firstOrNull { it.memberId.equals(user.id) && !it.isRemoved }

    fun getTicketMember(userId: String?) = _members.firstOrNull { it.memberId.equals(userId) }

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

    suspend fun openFromButton(): TicketCreateResult = TODO("Implement")
    suspend fun addTicketMessage(fromTicketAndMessage: TicketMessage): TicketMessage =
        TODO("Implement")
}
