package dev.slne.discordold.discord.interaction.command.commands.ticket

import dev.slne.discordold.annotation.DiscordCommandMeta
import dev.slne.discordold.discord.interaction.command.commands.TicketCommand
import dev.slne.discordold.exception.command.CommandExceptions
import dev.slne.discordold.guild.permission.CommandPermission
import dev.slne.discordold.message.translatable
import dev.slne.discordold.ticket.TicketCreator
import dev.slne.discordold.ticket.result.TicketCloseResult
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook

@DiscordCommandMeta(
    name = "wrong-ticket-type",
    description = "Closes a ticket while informing the user that they opened the wrong type of ticket.",
    permission = CommandPermission.TICKET_CLOSE
)
class TicketWrongTypeCommand(private val ticketCreator: TicketCreator) : TicketCommand() {

    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        val closer = interaction.user
        val ticket = interaction.getTicketOrThrow()

        val closeResult = ticketCreator.closeTicket(
            ticket,
            closer,
            translatable("interaction.command.ticket.wrong-type.close-reason")
        )

        if (closeResult != TicketCloseResult.SUCCESS) {
            throw CommandExceptions.TICKET_CLOSE.create(closeResult)
        }
    }
}