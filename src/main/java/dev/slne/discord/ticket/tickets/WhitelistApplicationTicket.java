package dev.slne.discord.ticket.tickets;

import java.util.Optional;

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
    public void afterOpen() {
        Optional<TextChannel> channelOptional = getChannel();

        if (channelOptional.isEmpty()) {
            return;
        }

        TextChannel channel = channelOptional.get();

        if (channel == null) {
            return;
        }

        getTicketAuthor().queue(author -> {
            String message = "Du möchtest dich auf dem Server whitelisten lassen? Bitte beachte die Vorrausetzungen in <#1124438644523012234>.";

            if (author != null) {
                message = author.getAsMention() + " | " + message;
            }

            channel.sendMessage(message).queue();
        });
    }

}