package dev.slne.discord.ticket.tickets;

import dev.slne.discord.ticket.TicketType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class BugReportTicket extends DescriptionTicket {

    /**
     * Constructor for a bug report ticket
     *
     * @param guild        The guild the ticket is created in
     * @param ticketAuthor The author of the ticket
     * @param description  The description of the ticket
     */
    public BugReportTicket(Guild guild, User ticketAuthor, String description) {
        super(guild, ticketAuthor, TicketType.BUGREPORT, description);
    }

    @Override
    public void afterOpen() {
        TextChannel channel = getChannel();

        if (channel == null) {
            return;
        }

        String message = "Wir freuen uns, dass du einen Fehler melden möchtest. Bitte beschreibe das Problem so genau wie möglich**. Wann? Wie? Wo? Screenshots und Videos des Fehlers sind gerne gesehen.";

        if (this.getTicketAuthor() != null) {
            message = this.getTicketAuthor().getAsMention() + " | " + message;
        }

        channel.sendMessage(message).queue(msg -> this.printDescription());
    }
}