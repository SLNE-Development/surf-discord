package dev.slne.discord.util

import dev.slne.discord.ticket.message.TicketMessage
import dev.slne.discord.ticket.message.attachment.TicketMessageAttachment
import dev.slne.discord.ticket.message.attachment.toTicketMessageAttachment
import net.dv8tion.jda.api.entities.Message

fun Message.toTicketMessage() {
    return TicketMessage().apply {
        this.messageId = message.id
        this.jsonContent = message.contentDisplay
        this.authorId = message.author.id
        this.authorName = message.author.name
        this.authorAvatarUrl = message.author.avatarUrl
        this.messageCreatedAt = message.timeCreated.toZonedDateTime()
        this.messageEditedAt = message.timeEdited?.toZonedDateTime()
        this.referencesMessageId = message.messageReference?.messageId
        this.botMessage = message.author.isBot

        message.attachments.map { it.toTicketMessageAttachment() }
            .forEach(::addAttachment)
    }
}

fun Message.Attachment.toTicketMessageAttachment() = TicketMessageAttachment(this)