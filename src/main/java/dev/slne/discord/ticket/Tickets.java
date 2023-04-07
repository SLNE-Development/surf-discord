package dev.slne.discord.ticket;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class Tickets {

    public static class Whitelist extends Ticket {

        private String minecraftName;

        public Whitelist(Guild guild, User ticketAuthor, String minecraftName) {
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

    public static abstract class DescriptionTicket extends Ticket {

        private String description;

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

    public static class ServerSupport extends DescriptionTicket {

        public ServerSupport(Guild guild, User ticketAuthor, String description) {
            super(guild, ticketAuthor, TicketType.SERVER_SUPPORT, description);
        }

        @Override
        public void afterOpen() {
            TextChannel channel = getChannel();

            if (channel == null) {
                return;
            }

            String message = "Willkommen beim Minecraft Server-Support!";

            if (this.getTicketAuthor() != null) {
                message = this.getTicketAuthor().getAsMention() + " | " + message;
            }

            channel.sendMessage(message).queue(v -> {
                this.printDescription();
            });
        }
    }

    public static class DiscordSupport extends DescriptionTicket {

        public DiscordSupport(Guild guild, User ticketAuthor, String description) {
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

    public static class BugReport extends DescriptionTicket {

        public BugReport(Guild guild, User ticketAuthor, String description) {
            super(guild, ticketAuthor, TicketType.BUGREPORT, description);
        }

        @Override
        public void afterOpen() {
            TextChannel channel = getChannel();

            if (channel == null) {
                return;
            }

            String message = "Wir freuen uns, dass du einen Fehler melden möchtest. Bitte beschreibe das Problem so genau wie möglich**. Wann? Wie? Wo? Screenshots und Videos des Fehlers sind gerne gesehen.";

            if (this.getTicketAuthor() != null) {
                message = this.getTicketAuthor().getAsMention() + " | " + message;
            }

            channel.sendMessage(message).queue(v -> {
                this.printDescription();
            });
        }
    }

}
