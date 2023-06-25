package dev.slne.discord.discord.interaction.button.buttons.ticket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dev.slne.discord.Launcher;
import dev.slne.discord.discord.interaction.button.DiscordButton;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketType;
import dev.slne.discord.ticket.result.TicketCreateResult;
import dev.slne.discord.ticket.tickets.BugReportTicket;
import dev.slne.discord.ticket.tickets.DiscordSupportTicket;
import dev.slne.discord.ticket.tickets.ServerSupportTicket;
import dev.slne.discord.ticket.tickets.WhitelistApplicationTicket;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public abstract class TicketButton extends DiscordButton {

    public static final String WHITELIST_TICKET_ID = "whitelist-ticket";
    public static final String SERVER_SUPPORT_TICKET_ID = "server-support-ticket";
    public static final String DISCORD_SUPPORT_TICKET_ID = "discord-support-ticket";
    public static final String BUGREPORT_TICKET_ID = "bugreport-ticket";

    private TicketType ticketType;

    /**
     * The TicketButton
     *
     * @param id         the id of the button
     * @param label      the label of the button
     * @param emoji      the emoji of the button
     * @param ticketType the ticket type of the button
     */
    protected TicketButton(@Nonnull String id, @Nonnull String label, @Nullable Emoji emoji,
            @Nonnull ButtonStyle style, @Nonnull TicketType ticketType) {
        super(id, label, emoji, style);

        this.ticketType = ticketType;
    }

    /**
     * The action of the button
     *
     * @param interaction the interaction
     */
    @Override
    @SuppressWarnings({ "java:S3776", "java:S1192" })
    public void onClick(ButtonInteraction interaction) {
        Ticket ticket = null;
        User user = interaction.getUser();
        Guild guild = interaction.getGuild();

        InteractionHook hook = interaction.deferReply(true).complete();

        switch (ticketType) {
            case WHITELIST:
                ticket = new WhitelistApplicationTicket(guild, user);
                break;
            case SERVER_SUPPORT:
                ticket = new ServerSupportTicket(guild, user);
                break;
            case BUGREPORT:
                ticket = new BugReportTicket(guild, user);
                break;
            case DISCORD_SUPPORT:
                ticket = new DiscordSupportTicket(guild, user);
                break;
            default:
                break;
        }

        if (ticket != null) {
            final Ticket finalTicket = ticket;

            ticket.openFromButton().whenComplete(result -> {
                if (result.equals(TicketCreateResult.SUCCESS)) {
                    StringBuilder message = new StringBuilder();
                    message.append("Dein \"");
                    message.append(this.ticketType.getName());
                    message.append("\"-Ticket wurde erfolgreich erstellt! ");

                    if (finalTicket.getChannel().isPresent()) {
                        message.append(finalTicket.getChannel().get().getAsMention());
                    }

                    String messageString = message.toString();
                    if (messageString != null) {
                        hook.editOriginal(messageString).queue();
                    }
                    return;
                } else if (result.equals(TicketCreateResult.ALREADY_EXISTS)) {
                    hook.editOriginal(
                            "Du hast bereits ein Ticket mit dem angegeben Typ geöffnet. Sollte dies nicht der Fall sein, wende dich per Ping an @notammo")
                            .queue();
                    return;
                } else {
                    hook.editOriginal("Es ist ein Fehler aufgetreten!").queue();
                    Launcher.getLogger().logError("Error while creating ticket: " + result);
                    return;
                }
            });
        } else {
            hook.editOriginal("Es konnte kein Ticket mit dem angegebenen Ticket-Typen erstellt werden!")
                    .queue();
        }
    }

    /**
     * @return the ticketType
     */
    public TicketType getTicketType() {
        return ticketType;
    }

    public static class WhitelistTicketButton extends TicketButton {
        /**
         * The WhitelistTicketButton
         */
        public WhitelistTicketButton() {
            super(WHITELIST_TICKET_ID, "Whitelist", Emoji.fromUnicode("U+1F512"), ButtonStyle.PRIMARY,
                    TicketType.WHITELIST);
        }
    }

    public static class ServerSupportTicketButton extends TicketButton {
        /**
         * The ServerSupportTicketButton
         */
        public ServerSupportTicketButton() {
            super(SERVER_SUPPORT_TICKET_ID, "Minecraft Server-Support", Emoji.fromUnicode("U+2696 U+FE0F"),
                    ButtonStyle.SUCCESS, TicketType.SERVER_SUPPORT);
        }
    }

    public static class DiscordSupportTicketButton extends TicketButton {
        /**
         * The DiscordSupportTicketButton
         */
        public DiscordSupportTicketButton() {
            super(DISCORD_SUPPORT_TICKET_ID, "Discord Server-Support",
                    Emoji.fromUnicode("U+1F9D1 U+200D U+2696 U+FE0F"),
                    ButtonStyle.SUCCESS, TicketType.DISCORD_SUPPORT);
        }
    }

    public static class BugreportTicketButton extends TicketButton {
        /**
         * The BugReportTicketButton
         */
        public BugreportTicketButton() {
            super(BUGREPORT_TICKET_ID, "Bugreport", Emoji.fromUnicode("U+1F41E"), ButtonStyle.DANGER,
                    TicketType.BUGREPORT);
        }
    }
}
