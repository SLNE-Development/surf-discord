package dev.slne.surf.discord.util

import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.ticket.TicketService
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.interactions.InteractionHook
import kotlin.random.Random

val random = Random

fun Long.relativeDiscordTimeStamp() = "<t:${this.div(1000)}:R>"
fun Long.absoluteDiscordTimeStamp() = "<t:${this.div(1000)}:F>"

suspend fun InteractionHook.asTicketOrNull() =
    getBean<TicketService>().getTicketByThreadId(this.interaction.channelIdLong)

suspend fun InteractionHook.asTicketOrThrow() =
    getBean<TicketService>().getTicketByThreadId(this.interaction.channelIdLong)
        ?: error("Ticket not found for thread ID ${this.interaction.channelIdLong}")

fun InteractionHook.replyError() = this.editOriginal("Ein Fehler ist aufgetreten.").queue()
fun InteractionHook.replyNoTicket() =
    this.editOriginal("Du befindest dich nicht in einem Ticket.").queue()

val Role.members get() = this.guild.members.filter { member -> member.roles.contains(this) }