package dev.slne.discord.ticket

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import dev.slne.discord.DiscordBot
import dev.slne.discord.config.discord.getGuildConfigByGuildId
import dev.slne.discord.config.role.getRoleConfig
import dev.slne.discord.message.Messages
import dev.slne.discord.ticket.member.TicketMember
import dev.slne.discord.ticket.message.TicketMessage
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

    @JsonProperty("id")
    var id: Long = -1

    @JsonProperty("ticket_id")
    var ticketId: UUID? = null

    @JsonProperty("opened_at")
    var openedAt: ZonedDateTime = ZonedDateTime.now()

    @JsonProperty("guild_id")
    var guildId: String? = null

    @JsonProperty("channel_id")
    var channelId: String? = null

    @JsonProperty("type")
    var ticketTypeString: String? = null

    @JsonProperty("author_id")
    var ticketAuthorId: String? = null

    @JsonProperty("author_name")
    var ticketAuthorName: String? = null

    @JsonProperty("author_avatar_url")
    var ticketAuthorAvatarUrl: String? = null

    @JsonProperty("closed_by_id")
    var closedById: String? = null

    @JsonProperty("closed_reason")
    var closedReason: String? = null

    @JsonProperty("closed_by_avatar_url")
    var closedByAvatarUrl: String? = null

    @JsonProperty("closed_by_name")
    var closedByName: String? = null

    @JsonProperty("closed_at")
    var closedAt: ZonedDateTime? = null

    @JsonProperty("messages")
    private val _messages = mutableListOf<TicketMessage>()

    @get:JsonIgnore
    val messages get() = _messages.toList()

    @JsonProperty("members")
    private val _members = mutableListOf<TicketMember>()

    @get:JsonIgnore
    val members get() = _members.toList()

    @JsonProperty("created_at")
    var createdAt: ZonedDateTime? = null

    init {
        if (guild != null) {
            this.guildId = guild.id
        }

        if (ticketType != null) {
            this.ticketTypeString = ticketType.name
        }

        if (ticketAuthor != null) {
            this.ticketAuthorName = ticketAuthor.name
            this.ticketAuthorId = ticketAuthor.id
            this.ticketAuthorAvatarUrl = ticketAuthor.avatarUrl
        }
    }

    fun addRawTicketMember(ticketMember: TicketMember) = _members.add(ticketMember)

    fun removeRawTicketMember(ticketMember: TicketMember) {
        _members.remove(ticketMember)
        removedMembers.add(ticketMember)
    }

    fun addRawTicketMessage(ticketMessage: TicketMessage) = _messages.add(ticketMessage)

    @JsonIgnore
    fun memberExists(user: User) = _members.any { it.memberId == user.id && !it.isRemoved }

    fun addTicketRole(role: Role) {
        val guildConfig = getGuildConfigByGuildId(guildId) ?: return
        val roleConfig = getRoleConfig(guildId, role.name) ?: return

        TODO("To be implemented when ticket channels are removed in favor for threads")
    }

    @JsonIgnore
    fun getTicketMessage(message: Message) = _messages.find { it.messageId.equals(message.id) }

    @JsonIgnore
    fun getTicketMessage(messageId: String?) = _messages.first { it.messageId.equals(messageId) }

    @JsonIgnore
    fun getTicketMember(user: User) = _members.first { it.memberId.equals(user.id) }

    @JsonIgnore
    fun getActiveTicketMember(user: User) =
        _members.first { it.memberId.equals(user.id) && !it.isRemoved }

    @JsonIgnore
    fun getTicketMember(userId: String?) = _members.first { it.memberId.equals(userId) }

    @get:JsonIgnore
    val ticketAuthor
        get() = ticketAuthorId?.let { DiscordBot.jda.retrieveUserById(it) }

    @get:JsonIgnore
    val ticketType
        get() = TicketType.valueOf(ticketTypeString!!)

    @get:JsonIgnore
    val guild
        get() = guildId?.let { DiscordBot.jda.getGuildById(it) }

    @get:JsonIgnore
    val channel
        get() = channelId?.let { DiscordBot.jda.getTextChannelById(it) }

    @get:JsonIgnore
    val closedBy
        get() = closedById?.let { DiscordBot.jda.retrieveUserById(it) }

    fun updateFrom(other: Ticket) {
        openedAt = other.openedAt
        ticketId = other.ticketId
        id = other.id
        createdAt = other.createdAt
        closedAt = other.closedAt
    }

    @get:JsonIgnore
    val isPersisted
        get() = id != -1L

    @JsonIgnore
    fun hasTicketId() = ticketId != null

    @JsonIgnore
    fun hasGuild() = guildId != null

    @get:JsonIgnore
    val closeReasonOrDefault: String
        get() = closedReason ?: Messages.DEFAULT_TICKET_CLOSED_REASON
}
