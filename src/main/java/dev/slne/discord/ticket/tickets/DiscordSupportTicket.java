package dev.slne.discord.ticket.tickets;

import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

/**
 * The type Discord support ticket.
 */
public class DiscordSupportTicket extends Ticket {

	/**
	 * Constructor for a discord support ticket
	 *
	 * @param guild        The guild the ticket is created in
	 * @param ticketAuthor The author of the ticket
	 */
	public DiscordSupportTicket(Guild guild, User ticketAuthor) {
		super(guild, ticketAuthor, TicketType.DISCORD_SUPPORT);
	}
}