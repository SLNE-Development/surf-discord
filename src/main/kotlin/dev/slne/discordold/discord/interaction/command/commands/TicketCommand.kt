package dev.slne.discordold.discord.interaction.command.commands

import dev.slne.discordold.discord.interaction.command.DiscordCommand
import dev.slne.discordold.exception.command.CommandExceptions
import dev.slne.discordold.extensions.getTicket
import dev.slne.discordold.extensions.getTicketOrThrow
import dev.slne.discordold.ticket.Ticket
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent


abstract class TicketCommand : DiscordCommand() {

    fun SlashCommandInteractionEvent.getThreadChannelOrThrow() =
        channel as? ThreadChannel ?: throw CommandExceptions.NO_THREAD_CHANNEL.create()

    suspend fun SlashCommandInteractionEvent.getTicketOrThrow() =
        getThreadChannelOrThrow().getTicketOrThrow()

    suspend fun SlashCommandInteractionEvent.getTicket(): Ticket? =
        getThreadChannelOrThrow().getTicket()
}
