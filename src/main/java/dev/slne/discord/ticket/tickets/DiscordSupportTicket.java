package dev.slne.discord.ticket.tickets;

import java.util.concurrent.CompletableFuture;

import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class DiscordSupportTicket extends Ticket {

    /**
     * Constructor for a discord support ticket
     *
     * @param guild        The guild the ticket is created in
     * @param ticketAuthor The author of the ticket
     */
    public DiscordSupportTicket(Guild guild, User ticketAuthor) {
        super(guild, ticketAuthor, TicketType.DISCORD_SUPPORT);
    }

    @Override
    public CompletableFuture<Void> afterOpen() {
        TextChannel channel = getChannel();

        if (channel == null) {
            return CompletableFuture.completedFuture(null);
        }

        getTicketAuthor().queue(author -> {
            String message = "Willkommen beim Discord Server-Support!";

            if (author != null) {
                message = author.getAsMention() + " | " + message;
            }

            channel.sendMessage(message).queue();
        });

        return CompletableFuture.completedFuture(null);
    }
}