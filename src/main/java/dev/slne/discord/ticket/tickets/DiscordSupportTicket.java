package dev.slne.discord.ticket.tickets;

import org.javalite.activejdbc.annotations.Table;

import dev.slne.discord.datasource.DiscordTables;
import dev.slne.discord.ticket.TicketType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@Table(DiscordTables.TICKETS)
public class DiscordSupportTicket extends DescriptionTicket {

    public DiscordSupportTicket() {
    }

    public DiscordSupportTicket(Guild guild, User ticketAuthor, String description) {
        super(guild, ticketAuthor, TicketType.DISCORD_SUPPORT, description);
    }

    @Override
    public void afterOpen() {
        TextChannel channel = getChannel();

        if (channel == null) {
            return;
        }

        String message = "Willkommen beim Discord Server-Support!";

        if (this.getTicketAuthor() != null) {
            message = this.getTicketAuthor().getAsMention() + " | " + message;
        }

        channel.sendMessage(message).queue(v -> {
            this.printDescription();
        });
    }
}