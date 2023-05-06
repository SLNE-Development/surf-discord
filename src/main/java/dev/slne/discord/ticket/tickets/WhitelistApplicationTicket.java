package dev.slne.discord.ticket.tickets;

import org.javalite.activejdbc.annotations.Table;

import dev.slne.discord.datasource.DiscordTables;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@Table(DiscordTables.TICKETS)
public class WhitelistApplicationTicket extends Ticket {

    private String minecraftName;

    public WhitelistApplicationTicket() {
    }

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