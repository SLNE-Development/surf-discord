package dev.slne.discord.util

import dev.slne.discord.ticket.message.TicketMessage
import dev.slne.discord.ticket.message.attachment.TicketMessageAttachment
import net.dv8tion.jda.api.entities.Message

fun Message.toTicketMessage() = TicketMessage(this)
fun Message.Attachment.toTicketMessageAttachment() = TicketMessageAttachment(this)