package dev.slne.discord.ticket.tickets;

import dev.slne.discord.config.ConfigUtil;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.concurrent.CompletableFuture;

/**
 * The type Bug report ticket.
 */
public class BugReportTicket extends Ticket {

	/**
	 * Constructor for a bug report ticket
	 *
	 * @param guild        The guild the ticket is created in
	 * @param ticketAuthor The author of the ticket
	 */
	public BugReportTicket(Guild guild, User ticketAuthor) {
		super(guild, ticketAuthor, TicketType.BUGREPORT);
	}

	@Override
	public CompletableFuture<Void> afterOpen() {
		TextChannel channel = getChannel();

		if (channel == null) {
			return CompletableFuture.completedFuture(null);
		}

		getTicketAuthor().queue(author -> {
			String message = ConfigUtil.getConfig().ticketConfig().bugreportMessage();
			message = message.replace("%author%", author.getAsMention());

			channel.sendMessage(message).queue();
		});

		printWlQueryEmbeds();

		return CompletableFuture.completedFuture(null);
	}
}