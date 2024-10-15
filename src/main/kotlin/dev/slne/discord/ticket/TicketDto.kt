package dev.slne.discord.ticket

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.DiscordBot
import dev.slne.discord.ticket.message.TicketMessageDto
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.TimeZoneStorage
import org.hibernate.annotations.TimeZoneStorageType
import org.hibernate.type.SqlTypes
import java.io.Serializable
import java.time.ZonedDateTime
import java.util.*

/**
 * DTO for {@link dev.slne.discord.ticket.Ticket}
 */
@Entity
@Table(name = "ticket_tickets")
open class TicketDto : Serializable {
    @Id
    @JdbcTypeCode(SqlTypes.BIGINT)
    @Column(name = "id", nullable = false)
    open var id: Long? = null
        protected set

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "ticket_id", unique = true, nullable = false, length = 36)
    open var ticketId: UUID? = null

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


    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "closed_by_id", length = 20)
    open var closedById: String? = null

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
//    @Lob
    @Column(name = "closed_reason")
    open var closedReason: String? = null


    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "closed_by_avatar_url")
    open var closedByAvatarUrl: String? = null

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "closed_by_name", length = 32)
    open var closedByName: String? = null

    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
    @Column(name = "closed_at")
    open var closedAt: ZonedDateTime? = null

    @OneToMany(mappedBy = "ticketDto")
    open var messages: MutableList<TicketMessageDto> = mutableListOf()


    suspend fun toTicket(): Ticket {
        val jda = DiscordBot.jda
        val guild = jda.getGuildById(guildId!!) ?: error("Guild not found")
        val author = jda.retrieveUserById(ticketAuthorId!!).await()

        val ticket = Ticket(
            guild,
            author,
            ticketType!!
        )

        ticket.dto = this

        return ticket
    }
}