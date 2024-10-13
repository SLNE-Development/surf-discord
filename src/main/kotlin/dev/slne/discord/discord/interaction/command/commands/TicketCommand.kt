package dev.slne.discord.discord.interaction.command.commands

import dev.slne.discord.discord.interaction.command.DiscordCommand
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.extensions.getTicketOrThrow
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent


abstract class TicketCommand : DiscordCommand() {
//    protected var ticket: Ticket? = null
//    protected var thread: ThreadChannel? = null
//
//    @MustBeInvokedByOverriders
//    override suspend fun performAdditionalChecks(
//        user: User,
//        guild: Guild,
//        interaction: SlashCommandInteractionEvent,
//        hook: InteractionHook?
//    ): Boolean {
//        val thread: ThreadChannel
//        try {
//            thread = interaction.getThreadChannelOrThrow()
//        } catch (e: CommandException) {
//            throw PreTicketCommandException(e)
//        }
//
//        this.ticket =
//            TicketService.getTicketByChannelId(thread.id) ?: throw PreTicketCommandException(
//                RawMessages.get("error.ticket.no-ticket-channel")
//            )
//
//        this.thread = thread
//
//        return true
//    }

    fun SlashCommandInteractionEvent.getThreadChannelOrThrow() =
        channel as? ThreadChannel ?: throw CommandExceptions.NO_THREAD_CHANNEL()

    fun SlashCommandInteractionEvent.getTicketOrThrow() =
        getThreadChannelOrThrow().getTicketOrThrow()
}
