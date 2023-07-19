package dev.slne.discord.ticket.tickets;

import java.util.concurrent.CompletableFuture;

import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

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

    @Override
    public CompletableFuture<Void> afterOpen() {
        TextChannel channel = getChannel();

        if (channel == null) {
            return CompletableFuture.completedFuture(null);
        }

        getTicketAuthor().queue(author -> {
            String message = "Vielen Dank für deine Whitelist Anfrage. Du wirst nun auf die Whitelist hinzugefügt, sobald jemand aus dem Team Zeit findet. Wir bitten um etwas Geduld.";

            if (author != null) {
                message = author.getAsMention() + " | " + message;
            }

            channel.sendMessage(message).queue();
        });

        return CompletableFuture.completedFuture(null);
    }

}