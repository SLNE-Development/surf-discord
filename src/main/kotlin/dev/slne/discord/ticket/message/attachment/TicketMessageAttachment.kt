package dev.slne.discord.ticket.message.attachment

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import dev.slne.discord.ticket.message.TicketMessage

class TicketMessageAttachment() {

    constructor(ticketMessage: TicketMessage) : this() {
        this.messageId = ticketMessage.id
    }

    @JsonProperty("id")
    val id: Long = 0

    @JsonProperty("name")
    var name: String? = null

    @JsonProperty("url")
    var url: String? = null

    @JsonProperty("extension")
    var extension: String? = null

    @JsonProperty("size")
    var size: Int? = null

    @JsonProperty("description")
    var description: String? = null

    @JsonProperty("message_id")
    var messageId: Long? = null

    @get:JsonIgnore
    val message: TicketMessage?
        get() = messageId?.let { TicketMessage.getByMessageId(it) }
}
