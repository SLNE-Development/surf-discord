package dev.slne.discord.ticket.tickets;

import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

/**
 * The type WhitelistDTO application ticket.
 */
public class WhitelistApplicationTicket extends Ticket {

	/**
	 * Constructor for a whitelist application ticket
	 *
	 * @param guild        The guild the ticket is created in
	 * @param ticketAuthor The author of the ticket
	 */
	public WhitelistApplicationTicket(Guild guild, User ticketAuthor) {
		super(guild, ticketAuthor, TicketType.WHITELIST);
	}

}