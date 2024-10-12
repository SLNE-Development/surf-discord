package dev.slne.discord.ticket.message.attachment

import dev.slne.discord.ticket.message.TicketMessage

class TicketMessageAttachment(
    ticketMessage: TicketMessage,
    val id: Long = 0,
    var name: String? = null,
    var url: String? = null,
    var extension: String? = null,
    var size: Int? = null,
    var description: String? = null
) {
    
    private var messageId: Long? = null

    val message: TicketMessage?
        get() = messageId?.let { TicketMessage.getByMessageId(it) }

    init {
        messageId = ticketMessage.id
    }
}
