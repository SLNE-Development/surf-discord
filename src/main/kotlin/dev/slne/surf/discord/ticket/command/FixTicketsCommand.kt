package dev.slne.surf.discord.ticket.command

import dev.minn.jda.ktx.coroutines.await
import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.ticket.database.ticket.TicketRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

@DiscordCommand(
    name = "ticket-fix",
    description = "Öffne archivierte Tickets, welche in der Datenbank noch offen sind."
)
object FixTicketsCommand : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.TICKET_FIX)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val rest = event.deferReply(true).await()

        val openTickets = TicketRepository.getOpenTickets()
        var fixedAmount = 0

        coroutineScope {
            openTickets.forEach { ticket ->
                async {
                    val channel = ticket.retrieveThreadChannel() ?: return@async

                    val rest = channel.sendMessage(".").await()
                    rest.delete().await()

                    fixedAmount++
                }
            }
        }

        rest.editOriginal(translatable("ticket.fix.done", fixedAmount.toString())).await()
    }
}
