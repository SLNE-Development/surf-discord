package dev.slne.discord.discord.interaction.button.buttons.ticket;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dev.slne.discord.Launcher;
import dev.slne.discord.datasource.database.future.DiscordFutureResult;
import dev.slne.discord.discord.interaction.button.DiscordButton;
import dev.slne.discord.discord.interaction.modal.modals.WhitelistTicketModal;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketType;
import dev.slne.discord.ticket.result.TicketCreateResult;
import dev.slne.discord.ticket.tickets.BugReportTicket;
import dev.slne.discord.ticket.tickets.DiscordSupportTicket;
import dev.slne.discord.ticket.tickets.ServerSupportTicket;
import dev.slne.discord.whitelist.Whitelist;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

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
        if (ticketType.equals(TicketType.WHITELIST)) {
            handleWhitelist(interaction);
            return;
        }

        interaction.deferReply(true).queue(hook -> {
            User user = interaction.getUser();
            Guild guild = interaction.getGuild();

            CompletableFuture<Ticket> ticketFuture = new CompletableFuture<>();
            DiscordFutureResult<Ticket> ticketResult = new DiscordFutureResult<>(ticketFuture);

            if (ticketType.equals(TicketType.DISCORD_SUPPORT)) {
                ticketFuture.complete(new DiscordSupportTicket(guild, user));
            } else {
                Whitelist.isWhitelisted(user).whenComplete(whitelistedBoolean -> {
                    boolean whitelisted = whitelistedBoolean;

                    List<TicketType> whitelistedTypes = List.of(TicketType.SERVER_SUPPORT,
                            TicketType.BUGREPORT);

                    if (!whitelisted && whitelistedTypes.contains(ticketType)) {
                        sendNotWhitelistedMessage(hook);
                        return;
                    }

                    Ticket ticket = null;
                    switch (ticketType) {
                        case SERVER_SUPPORT:
                            ticket = new ServerSupportTicket(guild, user);
                            break;
                        case BUGREPORT:
                            ticket = new BugReportTicket(guild, user);
                            break;
                        default:
                            break;
                    }

                    ticketFuture.complete(ticket);
                });
            }

            ticketResult.whenComplete(ticket -> {
                if (ticket == null) {
                    hook.editOriginal("Es konnte kein Ticket mit dem angegebenen Ticket-Typen erstellt werden!")
                            .queue();
                    return;
                }

                ticket.openFromButton().whenComplete(result -> {
                    if (result.equals(TicketCreateResult.SUCCESS)) {
                        StringBuilder message = new StringBuilder();
                        message.append("Dein \"");
                        message.append(this.ticketType.getName());
                        message.append("\"-Ticket wurde erfolgreich erstellt! ");

                        if (ticket.getChannel().isPresent()) {
                            message.append(ticket.getChannel().get().getAsMention());
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
                    } else if (result.equals(TicketCreateResult.MISSING_PERMISSIONS)) {
                        hook.editOriginal("Du hast nicht die benötigten Berechtigungen, um ein Ticket zu erstellen!")
                                .queue();
                        return;
                    } else {
                        hook.editOriginal("Es ist ein Fehler aufgetreten!").queue();
                        Launcher.getLogger().logError("Error while creating ticket: " + result);
                        return;
                    }
                });
            }, failure -> {
                hook.editOriginal("Es ist ein Fehler aufgetreten!").queue();
                Launcher.getLogger().logError("Error while creating ticket: ", failure);
            });
        });

    }

    /**
     * Handles the whitelist button
     *
     * @param interaction the interaction
     */
    private void handleWhitelist(ButtonInteraction interaction) {
        if (!ticketType.equals(TicketType.WHITELIST)) {
            return;
        }

        User user = interaction.getUser();

        Whitelist.isWhitelisted(user).whenComplete(whitelistedBoolean -> {
            boolean whitelisted = whitelistedBoolean;

            if (whitelisted) {
                sendAllreadyWhitelistedMessage(interaction);
                return;
            }

            WhitelistTicketModal whitelistModal = new WhitelistTicketModal();
            Modal modal = whitelistModal.buildModal();
            interaction.replyModal(modal).queue();
        }, failure -> {
            failure.printStackTrace();
            interaction.reply("Es ist ein Fehler aufgetreten!").queue();
        });
    }

    /**
     * Sends a message to the user that he is not whitelisted
     *
     * @param hook the hook
     */
    private void sendNotWhitelistedMessage(InteractionHook hook) {
        hook.editOriginal("Du befindest dich nicht auf der Whitelist und kannst dieses Ticket nicht öffnen.").queue();
    }

    /**
     * Sends a message to the user that he is allready whitelisted
     *
     * @param hook the hook
     */
    private void sendAllreadyWhitelistedMessage(ButtonInteraction interaction) {
        interaction.reply("Du befindest dich bereits auf der Whitelist und kannst dieses Ticket nicht öffnen.")
                .setEphemeral(true).queue();
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
