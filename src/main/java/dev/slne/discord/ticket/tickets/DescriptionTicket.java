package dev.slne.discord.ticket.tickets;

import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public abstract class DescriptionTicket extends Ticket {

    private String description;

    public DescriptionTicket() {

    }

    public DescriptionTicket(Guild guild, User ticketAuthor, TicketType ticketType, String description) {
        super(guild, ticketAuthor, ticketType);

        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void printDescription() {
        TextChannel channel = getChannel();

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