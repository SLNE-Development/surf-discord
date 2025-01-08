package dev.slne.discord.util

import dev.slne.discord.ticket.message.TicketMessage
import dev.slne.discord.ticket.message.attachment.TicketMessageAttachment
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User

suspend fun Message.toTicketMessage() = TicketMessage.fromMessage(this)
fun Message.Attachment.toTicketMessageAttachment() = TicketMessageAttachment(this)

fun User.memberOrNull(guild: Guild) = guild.getMember(this)
fun User.member(guild: Guild) = memberOrNull(guild) ?: error("User is not a member of the guild")
