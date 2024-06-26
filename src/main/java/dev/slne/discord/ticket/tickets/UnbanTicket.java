package dev.slne.discord.ticket.tickets;

import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

/**
 * The type unban ticket.
 */
public class UnbanTicket extends Ticket {

	/**
	 * Constructor for a unban ticket
	 *
	 * @param guild        The guild the ticket is created in
	 * @param ticketAuthor The author of the ticket
	 */
	public UnbanTicket(Guild guild, User ticketAuthor) {
		super(guild, ticketAuthor, TicketType.UNBAN);
	}
}