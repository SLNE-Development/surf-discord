package dev.slne.discord.ticket.tickets;

import java.util.concurrent.CompletableFuture;

import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

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
            String message = "Wir freuen uns, dass du einen Fehler melden möchtest. **Bitte beschreibe das Problem so genau wie möglich**. Wann? Wie? Wo? Screenshots und Videos des Fehlers sind gerne gesehen.";

            if (author != null) {
                message = author.getAsMention() + " | " + message;
            }

            channel.sendMessage(message).queue();
        });

        printWlQueryEmbeds();

        return CompletableFuture.completedFuture(null);
    }
}