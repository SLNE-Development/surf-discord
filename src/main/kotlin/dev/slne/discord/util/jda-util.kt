package dev.slne.discord.util

import dev.slne.discord.ticket.message.TicketMessage
import dev.slne.discord.ticket.message.attachment.TicketMessageAttachment
import net.dv8tion.jda.api.entities.Message

suspend fun Message.toTicketMessage() = TicketMessage.fromMessage(this)
fun Message.Attachment.toTicketMessageAttachment() = TicketMessageAttachment(this)