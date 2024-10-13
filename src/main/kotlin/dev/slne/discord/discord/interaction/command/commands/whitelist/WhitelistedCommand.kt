package dev.slne.discord.discord.interaction.command.commands.whitelist

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.commands.TicketCommand
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.RawMessages
import dev.slne.discord.ticket.Ticket
import dev.slne.discord.ticket.TicketCreator
import dev.slne.discord.ticket.result.TicketCloseResult
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook

@DiscordCommandMeta(
    name = "whitelisted",
    description = "Schließt ein Ticket mit der Begründung, dass der Nutzer whitelisted wurde.",
    permission = CommandPermission.WHITELISTED
)
object WhitelistedCommand : TicketCommand() {

    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        hook.editOriginal(RawMessages.get("interaction.command.ticket.close.closing")).await()

        closeTicket(interaction.user, interaction.getTicketOrThrow())
    }

    private suspend fun closeTicket(closer: User, ticket: Ticket) {
        val closeResult = TicketCreator.closeTicket(
            ticket,
            closer,
            RawMessages.get("interaction.command.ticket.whitelisted.close-reason")
        )

        if (closeResult != TicketCloseResult.SUCCESS) {
            throw CommandExceptions.TICKET_CLOSE(closeResult)
        }
    }
}
