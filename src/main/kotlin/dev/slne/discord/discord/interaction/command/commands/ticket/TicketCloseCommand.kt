package dev.slne.discord.discord.interaction.command.commands.ticket

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.commands.TicketCommand
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.translatable
import dev.slne.discord.ticket.Ticket
import dev.slne.discord.ticket.TicketCreator
import dev.slne.discord.ticket.result.TicketCloseResult
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook


private const val REASON_OPTION = "reason"

@DiscordCommandMeta(
    name = "close",
    description = "Closes a ticket.",
    permission = CommandPermission.TICKET_CLOSE
)
class TicketCloseCommand(private val ticketCreator: TicketCreator) : TicketCommand() {

    override val options = listOf(
        option<String>(
            REASON_OPTION,
            translatable("interaction.command.ticket.close.arg.reason"),
        )
    )

    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        val closer = interaction.user
        val reason = interaction.getOptionOrThrow<String>(
            REASON_OPTION,
            exceptionMessage = "You must provide a reason."
        )
        val ticket = interaction.getTicketOrThrow()

        hook.editOriginal(translatable("interaction.command.ticket.close.closing")).await()
        closeTicket(closer, ticket, reason, hook)
    }

    private suspend fun closeTicket(
        closer: User,
        ticket: Ticket,
        closeReason: String,
        hook: InteractionHook
    ) {
        val closeResult = ticketCreator.closeTicket(ticket, closer, closeReason)

        if (closeResult != TicketCloseResult.SUCCESS) {
            throw CommandExceptions.TICKET_CLOSE.create(closeResult)
        }

        hook.deleteOriginal().await()
    }
}
