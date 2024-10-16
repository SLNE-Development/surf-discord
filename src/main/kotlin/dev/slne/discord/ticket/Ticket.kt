package dev.slne.discord.ticket

import dev.slne.discord.DiscordBot
import dev.slne.discord.message.Messages
import dev.slne.discord.persistence.service.ticket.TicketService
import dev.slne.discord.ticket.message.TicketMessage
import dev.slne.discord.ticket.result.TicketCreateResult
import jakarta.persistence.*
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.TimeZoneStorage
import org.hibernate.annotations.TimeZoneStorageType
import org.hibernate.type.SqlTypes
import java.time.ZonedDateTime
import java.util.*

@Suppress("JpaDataSourceORMInspection")
@Entity
@Table(name = "ticket_tickets")
open class Ticket protected constructor() {

    @Id
    @JdbcTypeCode(SqlTypes.BIGINT)
    @Column(name = "id", nullable = false)
    open var id: Long? = null
        protected set

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "ticket_id", nullable = false, unique = true, length = 36)
    open var ticketId: UUID? = null
        protected set

    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
    @Column(name = "opened_at", nullable = false)
    open var openedAt: ZonedDateTime? = null
        protected set

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "guild_id", length = 20, nullable = false)
    open var guildId: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "thread_id", length = 20)
    open var threadId: String? = null

    @JdbcTypeCode(SqlTypes.CHAR)
    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_type")
    open var ticketType: TicketType? = null
        protected set

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "ticket_author_id", length = 20)
    open var ticketAuthorId: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "ticket_author_name", length = 64)
    open var ticketAuthorName: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "ticket_author_avatar_url")
    open var ticketAuthorAvatarUrl: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "closed_by_id", length = 20)
    open var closedById: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Lob
    @Column(name = "closed_reason")
    open var closedReason: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "closed_by_avatar_url")
    open var closedByAvatarUrl: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "closed_by_name", length = 32)
    open var closedByName: String? = null
        protected set

    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
    @Column(name = "closed_at")
    open var closedAt: ZonedDateTime? = null
        protected set

    @OneToMany(mappedBy = "ticket")
    protected open var _messages: MutableList<TicketMessage> = mutableListOf()

    val messages: List<TicketMessage> get() = _messages
    val thread get() = threadId?.let { DiscordBot.jda.getThreadChannelById(it) }
    val closedBy get() = closedById?.let { DiscordBot.jda.retrieveUserById(it) }
    val closeReasonOrDefault: String get() = closedReason ?: Messages.DEFAULT_TICKET_CLOSED_REASON
    val author get() = ticketAuthorId?.let { DiscordBot.jda.retrieveUserById(it) }
    val guild get() = guildId?.let { DiscordBot.jda.getGuildById(it) }

    fun addTicketMessage(ticketMessage: TicketMessage) {
        _messages.add(ticketMessage)
        ticketMessage.ticket = this
    }

    fun getTicketMessage(message: Message) = messages.find { it.messageId.equals(message.id) }
    fun getTicketMessage(messageId: String) = messages.find { it.messageId == messageId }

    suspend fun openFromButton(): TicketCreateResult = TicketCreator.openTicket(this)
    suspend fun save(): Ticket = TicketService.saveTicket(this)
    suspend fun create(): Ticket = TicketService.createTicket(this)

    fun close(
        closedBy: User,
        closedReason: String,
        closedAt: ZonedDateTime = ZonedDateTime.now()
    ) {
        this.closedById = closedBy.id
        this.closedByName = closedBy.name
        this.closedByAvatarUrl = closedBy.avatarUrl
        this.closedReason = closedReason
        this.closedAt = closedAt
    }

    companion object {
        fun open(
            guild: Guild,
            ticketAuthor: User,
            ticketType: TicketType,
            openedAt: ZonedDateTime = ZonedDateTime.now()
        ) = Ticket().apply {
            this.ticketId = UUID.randomUUID()
            this.openedAt = openedAt
            this.guildId = guild.id
            this.ticketType = ticketType
            this.ticketAuthorId = ticketAuthor.id
            this.ticketAuthorName = ticketAuthor.name
            this.ticketAuthorAvatarUrl = ticketAuthor.avatarUrl
        }
    }
}