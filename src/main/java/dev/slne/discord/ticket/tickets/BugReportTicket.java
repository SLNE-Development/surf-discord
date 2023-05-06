package dev.slne.discord.ticket.tickets;

import org.javalite.activejdbc.annotations.Table;

import dev.slne.discord.datasource.DiscordTables;
import dev.slne.discord.ticket.TicketType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@Table(DiscordTables.TICKETS)
public class BugReportTicket extends DescriptionTicket {

    public BugReportTicket() {
        
    }

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

        channel.sendMessage(message).queue(v -> {
            this.printDescription();
        });
    }
}