package dev.slne.discordold.discord.interaction.command.commands.whitelist

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discordold.annotation.DiscordCommandMeta
import dev.slne.discordold.discord.interaction.command.commands.TicketCommand
import dev.slne.discordold.exception.command.CommandExceptions
import dev.slne.discordold.guild.permission.CommandPermission
import dev.slne.discordold.message.translatable
import dev.slne.discordold.ticket.Ticket
import dev.slne.discordold.ticket.TicketCreator
import dev.slne.discordold.ticket.result.TicketCloseResult
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook

@DiscordCommandMeta(
    name = "whitelisted",
    description = "Schließt ein Ticket mit der Begründung, dass der Nutzer whitelisted wurde.",
    permission = CommandPermission.WHITELISTED
)
class WhitelistedCommand(private val ticketCreator: TicketCreator) : TicketCommand() {

    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        hook.editOriginal(translatable("interaction.command.ticket.close.closing")).await()

        closeTicket(interaction.user, interaction.getTicketOrThrow())
    }

    private suspend fun closeTicket(closer: User, ticket: Ticket) {
        val closeResult = ticketCreator.closeTicket(
            ticket,
            closer,
            translatable("interaction.command.ticket.whitelisted.close-reason")
        )

        if (closeResult != TicketCloseResult.SUCCESS) {
            throw CommandExceptions.TICKET_CLOSE.create(closeResult)
        }
    }
}
