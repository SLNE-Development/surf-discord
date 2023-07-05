package dev.slne.discord.ticket.tickets;

import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

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
    public void afterOpen() {
        TextChannel channel = getChannel();

        if (channel == null) {
            return;
        }

        getTicketAuthor().queue(author -> {
            String message = "Willkommen beim Minecraft Server-Support!";

            if (author != null) {
                message = author.getAsMention() + " | " + message;
            }

            channel.sendMessage(message).queue();
        });
    }
}