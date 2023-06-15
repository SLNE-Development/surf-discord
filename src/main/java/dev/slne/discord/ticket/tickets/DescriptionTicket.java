package dev.slne.discord.ticket.tickets;

import java.util.Optional;

import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public abstract class DescriptionTicket extends Ticket {

    private String description;

    /**
     * Constructor for a description ticket
     *
     * @param guild        The guild the ticket is created in
     * @param ticketAuthor The author of the ticket
     * @param ticketType   The type of the ticket
     * @param description  The description of the ticket
     */
    protected DescriptionTicket(Guild guild, User ticketAuthor, TicketType ticketType, String description) {
        super(guild, ticketAuthor, ticketType);

        this.description = description;
    }

    /**
     * Returns the description of the ticket
     *
     * @return The description of the ticket
     */
    public String getDescription() {
        return description;
    }

    /**
     * Prints the description of the ticket
     */
    public void printDescription() {
        Optional<TextChannel> channelOptional = getChannel();

        if (channelOptional.isEmpty()) {
            return;
        }

        TextChannel channel = channelOptional.get();

        if (channel == null) {
            return;
        }

        String message = "```" + this.getDescription() + "```";

        if (this.getTicketAuthor() != null) {
            message = this.getTicketAuthor().getAsMention() + " schrieb" + message;
        }

        channel.sendMessage(message).queue();
    }
}