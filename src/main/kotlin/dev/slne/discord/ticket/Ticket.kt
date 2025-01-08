package dev.slne.discord.ticket

import dev.slne.discord.getBean
import dev.slne.discord.message.Messages
import dev.slne.discord.ticket.message.TicketMessage
import dev.slne.discord.util.freeze
import dev.slne.discord.util.toObjectList
import jakarta.persistence.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.TimeZoneStorage
import org.hibernate.annotations.TimeZoneStorageType
import org.hibernate.proxy.HibernateProxy
import org.hibernate.type.SqlTypes
import java.time.ZonedDateTime
import java.util.*

@Entity
@Table(name = "ticket_tickets")
data class Ticket(
    @Id
    @JdbcTypeCode(SqlTypes.BIGINT)
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "ticket_id", nullable = false, unique = true, length = 36)
    val ticketId: UUID,

    @JdbcTypeCode(SqlTypes.CHAR)
    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_type")
    val ticketType: TicketType,

    @Transient
    val ticketAuthor: User,

    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
    @Column(name = "opened_at", nullable = false)
    val openedAt: ZonedDateTime,

    @Transient
    val internalGuild: Guild
) {

    constructor(
        guild: Guild,
        author: User,
        ticketType: TicketType,
    ) : this(
        ticketId = UUID.randomUUID(),
        ticketType = ticketType,
        ticketAuthor = author,
        openedAt = ZonedDateTime.now(),
        internalGuild = guild
    )

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "guild_id", length = 20)
    val guildId: String = internalGuild.id

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "thread_id", length = 20)
    var threadId: String? = null

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "ticket_author_id", length = 20, nullable = false)
    val ticketAuthorId = ticketAuthor.id

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "ticket_author_name", length = 64, nullable = false)
    val ticketAuthorName = ticketAuthor.name

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "ticket_author_avatar_url")
    val ticketAuthorAvatarUrl = ticketAuthor.avatarUrl

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "closed_by_id", length = 20)
    var closedById: String? = null

    @Lob
    @Column(name = "closed_reason")
    var closedReason: String? = null

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "closed_by_avatar_url")
    var closedByAvatarUrl: String? = null

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "closed_by_name", length = 32)
    var closedByName: String? = null

    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
    @Column(name = "closed_at")
    var closedAt: ZonedDateTime? = null

    @Transient
    var isClosing = false

    @OneToMany(
        mappedBy = "ticket",
        cascade = [CascadeType.ALL],
        fetch = FetchType.EAGER,
    )
    private var _messages = mutableListOf<TicketMessage>()
    val messages get() = _messages.toObjectList().freeze()

    val thread get() = threadId?.let { getBean<JDA>().getThreadChannelById(it) }
    val closedBy get() = closedById?.let { getBean<JDA>().retrieveUserById(it) }
    val closeReasonOrDefault: String get() = closedReason ?: Messages.DEFAULT_TICKET_CLOSED_REASON
    val author get() = getBean<JDA>().retrieveUserById(ticketAuthorId)
    val guild get() = getBean<JDA>().getGuildById(guildId)

    val isClosed
        get() = closedAt != null

    fun addMessage(message: TicketMessage) {
        _messages.add(message)
        message.ticket = this
    }

    fun getTicketMessage(message: Message) = getTicketMessage(message.id)
    fun getTicketMessage(messageId: String) = _messages.firstOrNull { it.messageId == messageId }

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

    fun reopen() {
        this.closedById = null
        this.closedByName = null
        this.closedByAvatarUrl = null
        this.closedReason = null
        this.closedAt = null
    }

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Ticket

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    override fun toString(): String {
        return "Ticket(_messages=$_messages, isClosing=$isClosing, closedAt=$closedAt, closedByName=$closedByName, closedByAvatarUrl=$closedByAvatarUrl, closedReason=$closedReason, closedById=$closedById, ticketAuthorAvatarUrl=$ticketAuthorAvatarUrl, ticketAuthorName='$ticketAuthorName', ticketAuthorId='$ticketAuthorId', threadId=$threadId, guildId='$guildId', internalGuild=$internalGuild, openedAt=$openedAt, ticketAuthor=$ticketAuthor, ticketType=$ticketType, ticketId=$ticketId, id=$id)"
    }

}