package dev.slne.discord.ticket.tickets;

import dev.slne.discord.config.ConfigUtil;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.concurrent.CompletableFuture;

/**
 * The type Server support ticket.
 */
public class ServerSupportTicket extends Ticket {

	/**
	 * Constructor for a server support ticket
	 *
	 * @param guild        The guild the ticket is created in
	 * @param ticketAuthor The author of the ticket
	 */
	public ServerSupportTicket(Guild guild, User ticketAuthor) {
		super(guild, ticketAuthor, TicketType.SERVER_SUPPORT);
	}

	@Override
	public CompletableFuture<Void> afterOpen() {
		TextChannel channel = getChannel();

		if (channel == null) {
			return CompletableFuture.completedFuture(null);
		}

		getTicketAuthor().queue(author -> {
			String message = ConfigUtil.getConfig().ticketConfig().serverSupportMessage();
			message = message.replace("%author%", author.getAsMention());

			channel.sendMessage(message).queue();
		});

		printWlQueryEmbeds();

		return CompletableFuture.completedFuture(null);
	}
}