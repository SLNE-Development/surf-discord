package dev.slne.discord.ticket

import dev.slne.discord.DiscordBot
import dev.slne.discord.message.Messages
import dev.slne.discord.ticket.message.TicketMessage
import dev.slne.discord.ticket.result.TicketCreateResult
import jakarta.persistence.*
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import java.util.*
import kotlin.reflect.KProperty

class Ticket(
    val guild: Guild,
    val ticketAuthor: User,
    val ticketType: TicketType
) {

    var dto: TicketDto? = null

    fun addRawTicketMessage(ticketMessage: TicketMessage) = _messages.add(ticketMessage)

    fun addTicketRole(role: Role) {
        TODO("To be implemented when ticket channels are removed in favor for threads")
//        val guildConfig = getGuildConfigByGuildId(guildId) ?: return
//        val roleConfig = getRoleConfig(guildId, role.name) ?: return

    }

    fun getTicketMessage(message: Message) = _messages.find { it.messageId.equals(message.id) }

    fun getTicketMessage(messageId: String?) =
        _messages.firstOrNull { it.messageId.equals(messageId) }


    val thread
        get() = threadId?.let { DiscordBot.jda.getThreadChannelById(it) }

    val closedBy
        get() = closedById?.let { DiscordBot.jda.retrieveUserById(it) }

    fun hasTicketId() = ticketId != null

    fun hasGuild() = guildId != null

    val closeReasonOrDefault: String
        get() = closedReason ?: Messages.DEFAULT_TICKET_CLOSED_REASON

    suspend fun openFromButton(): TicketCreateResult = TicketCreator.openTicket(this)


    suspend fun addTicketMessage(fromTicketAndMessage: TicketMessage): TicketMessage =
        fromTicketAndMessage // TODO: Implement

    suspend fun save(): Ticket = this // TODO: Implement
}

class DtoDelegate<T>(private val getter: () -> T) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = getter()
}

