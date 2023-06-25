package dev.slne.discord.ticket.tickets;

import java.util.Optional;

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
    public void afterOpen() {
        Optional<TextChannel> channelOptional = getChannel();

        if (channelOptional.isEmpty()) {
            return;
        }

        TextChannel channel = channelOptional.get();

        if (channel == null) {
            return;
        }

        String message = "Willkommen beim Discord Server-Support!";

        if (this.getTicketAuthor() != null) {
            message = this.getTicketAuthor().getAsMention() + " | " + message;
        }

        channel.sendMessage(message).queue();
    }
}