package dev.slne.discord.ticket

import dev.slne.discord.DiscordBot
import dev.slne.discord.message.Messages
import dev.slne.discord.persistence.converter.DiscordGuildConverter
import dev.slne.discord.persistence.converter.DiscordThreadChannelConverter
import dev.slne.discord.persistence.converter.DiscordUserConverter
import dev.slne.discord.persistence.service.ticket.TicketService
import dev.slne.discord.ticket.message.TicketMessage
import dev.slne.discord.ticket.result.TicketCreateResult
import jakarta.persistence.*
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.requests.RestAction
import java.time.ZonedDateTime
import java.util.*

@Entity
@Table(name = "ticket_tickets")
data class Ticket(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(nullable = false)
    val ticketId: UUID,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val ticketType: TicketType,

    @Column(nullable = true, name = "author_id")
    @Convert(converter = DiscordUserConverter::class)
    val author: User?,

    @Column(nullable = true, name = "thread_id")
    @Convert(converter = DiscordThreadChannelConverter::class)
    val thread: ThreadChannel?,

    @Column(nullable = true, name = "guild_id")
    @Convert(converter = DiscordGuildConverter::class)
    val guild: Guild?
) {
    constructor() : this(null, UUID.randomUUID(), TicketType.DISCORD_SUPPORT, null, null, null)

    var openedAt: ZonedDateTime = ZonedDateTime.now()

    var ticketAuthorName: String?
    var ticketAuthorAvatarUrl: String?

    var closedById: String? = null

    val closedBy: RestAction<User>?
        get() = closedById?.let { DiscordBot.jda.retrieveUserById(it) }

    var closedReason: String? = null

    var closedByAvatarUrl: String? = null

    var closedByName: String? = null

    var closedAt: ZonedDateTime? = null

    @OneToMany(mappedBy = "ticketId", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    private val _messages = mutableListOf<TicketMessage>()
    val messages get() = _messages.toList()

    var createdAt: ZonedDateTime? = null

    init {
        ticketAuthorName = author?.name
        ticketAuthorAvatarUrl = author?.avatarUrl
    }

    fun addRawTicketMessage(ticketMessage: TicketMessage) = _messages.add(ticketMessage)

    fun getTicketMessage(message: Message) = _messages.find { it.messageId.equals(message.id) }

    fun getTicketMessage(messageId: String?) =
        _messages.firstOrNull { it.messageId.equals(messageId) }

    val closeReasonOrDefault: String
        get() = closedReason ?: Messages.DEFAULT_TICKET_CLOSED_REASON

    suspend fun openFromButton(): TicketCreateResult = TicketCreator.openTicket(this)

    suspend fun addTicketMessage(fromTicketAndMessage: TicketMessage): TicketMessage =
        fromTicketAndMessage // TODO: Implement

    suspend fun save(): Ticket = TicketService.saveTicket(this)

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false

        val oEffectiveClass = other.javaClass
        val thisEffectiveClass = this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Ticket

        return id != null && id == other.id
    }

    final override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  id = $id )"
    }
}
