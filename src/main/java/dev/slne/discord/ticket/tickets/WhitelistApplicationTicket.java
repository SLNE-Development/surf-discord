package dev.slne.discord.ticket.tickets;

import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class WhitelistApplicationTicket extends Ticket {

    private String minecraftName;

    /**
     * Constructor for a whitelist application ticket
     *
     * @param guild         The guild the ticket is created in
     * @param ticketAuthor  The author of the ticket
     * @param minecraftName The minecraft name of the ticket
     */
    public WhitelistApplicationTicket(Guild guild, User ticketAuthor, String minecraftName) {
        super(guild, ticketAuthor, TicketType.WHITELIST);

        this.minecraftName = minecraftName;
    }

    @Override
    public void afterOpen() {
        TextChannel channel = getChannel();

        if (channel == null) {
            return;
        }

        String message = "Du möchtest dich auf dem Server whitelisten lassen? Bitte beachte die Vorrausetzungen in <#983479094983397406>.";

        if (this.getTicketAuthor() != null) {
            message = this.getTicketAuthor().getAsMention() + " | " + message;
        }

        channel.sendMessage(message).queue();
    }

    public String getMinecraftName() {
        return minecraftName;
    }

}