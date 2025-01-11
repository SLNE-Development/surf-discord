package dev.slne.discord.discord.interaction.command.commands

import dev.slne.discord.discord.interaction.command.DiscordCommand
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.extensions.getTicket
import dev.slne.discord.extensions.getTicketOrThrow
import dev.slne.discord.ticket.Ticket
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
