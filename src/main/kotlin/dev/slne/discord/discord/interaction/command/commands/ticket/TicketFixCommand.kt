package dev.slne.discord.discord.interaction.command.commands.ticket

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.commands.TicketCommand
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.persistence.service.ticket.TicketService
import dev.slne.discord.ticket.Ticket
import dev.slne.discord.ticket.TicketType
import dev.slne.discord.ticket.message.TicketMessage
import net.dv8tion.jda.api.entities.MessageHistory
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook

@DiscordCommandMeta(
    name = "fix",
    description = "Fixes a ticket that was not created properly.",
    permission = CommandPermission.TICKET_BUTTONS,
    ephemeral = true,
    guildOnly = true,
    nsfw = false
)
class TicketFixCommand(private val ticketService: TicketService) : TicketCommand() {

    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        hook.editOriginal("Gathering ticket data...").await()

        val interactionTicket = interaction.getTicket()

        if (interactionTicket != null) {
            hook.editOriginal("Ticket is not null, save via /close")
            return
        }

        val guild = interaction.guild
        if (guild == null) {
            hook.editOriginal("Guild is null, cannot create ticket").await()
            return
        }

        hook.editOriginal("Ticket is null, creating new ticket...").await()

        val channel = interaction.channel as ThreadChannel
        val channelName = channel.name
        val ticketType = TicketType.fromChannelName(channelName)

        val split = channelName.split("-")
        val members = guild.retrieveMembersByPrefix(split[1], 1).await()
        val ticketAuthor = members.firstOrNull()?.user

        if (ticketAuthor == null) {
            hook.editOriginal("Ticket author is null, cannot create ticket").await()
            return
        }

        val ticket = Ticket(
            guild = guild,
            author = ticketAuthor,
            ticketType = ticketType
        )

        ticket.threadId = channel.id

        val history = MessageHistory.getHistoryFromBeginning(channel).await()
        val messages = history.retrievedHistory
        val ticketMessages = messages.map { TicketMessage.fromMessage(it) }
        ticketMessages.forEach { ticket.addMessage(it) }

        ticketService.saveTicket(ticket)

        hook.editOriginal("Ticket created!").await()
    }
}