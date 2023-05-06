package dev.slne.discord.ticket.modals;

import java.util.Arrays;
import java.util.List;

import dev.slne.data.core.database.worker.ConnectionWorkers;
import dev.slne.discord.interaction.modal.DiscordModal;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketType;
import dev.slne.discord.ticket.result.TicketCreateResult;
import dev.slne.discord.ticket.tickets.BugReportTicket;
import dev.slne.discord.ticket.tickets.DiscordSupportTicket;
import dev.slne.discord.ticket.tickets.ServerSupportTicket;
import dev.slne.discord.ticket.tickets.WhitelistApplicationTicket;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;

public abstract class TicketModal extends DiscordModal {

    private TicketType ticketType;

    public TicketModal() {

    }

    public TicketModal(TicketType ticketType) {
        super(ticketType.getName() + " Ticket erstellen");

        this.ticketType = ticketType;
    }

    @Override
    public void fillComponents() {
        if (ticketType.equals(TicketType.WHITELIST)) {
            TextInput minecraftNameInput = TextInput.create("minecraft-name", "Minecraft-Name", TextInputStyle.SHORT)
                    .setRequired(true).build();

            TextInput discordTwitchVerified = TextInput.create("discord-twitch-verified",
                    "Twitch-Account verbunden?", TextInputStyle.SHORT)
                    .setPlaceholder("Ja").setValue("Nein").setRequired(true).build();

            components.add(minecraftNameInput);
            components.add(discordTwitchVerified);
        } else if (ticketType.equals(TicketType.SERVER_SUPPORT) || ticketType.equals(TicketType.DISCORD_SUPPORT)
                || ticketType.equals(TicketType.BUGREPORT)) {
            TextInput description = TextInput.create("body",
                    "Beschreibung", TextInputStyle.PARAGRAPH).setMinLength(20)
                    .setPlaceholder("Ich wurde gebannt, weil...").setRequired(true).build();

            components.add(description);
        }
    }

    @Override
    public void execute(ModalInteractionEvent event) {
        ModalInteraction modalInteraction = event.getInteraction();

        this.ticketType = TicketType.getByName(modalInteraction.getModalId());
        if (this.ticketType == null) {
            event.reply("Es ist ein Fehler beim abrufen des Ticket-Typs aufgetreten.").setEphemeral(true).queue();
            return;
        }

        InteractionHook hook = modalInteraction.deferReply(true).complete();

        ConnectionWorkers.asyncVoid(() -> {
            Ticket ticket = null;

            if (ticketType.equals(TicketType.WHITELIST)) {
                String minecraftName = modalInteraction.getValue("minecraft-name").getAsString();
                String discordTwitchVerifiedString = modalInteraction.getValue("discord-twitch-verified").getAsString();

                List<String> validInput = Arrays.asList("ja", "yes", "j", "y");
                boolean discordTwitchVerified = validInput.contains(discordTwitchVerifiedString.toLowerCase());

                if (!discordTwitchVerified) {
                    hook.editOriginal(
                            "Dein Twitch-Account muss mit deinem Discord-Account verknüpft sein, um auf dem Server spielen zu dürfen.")
                            .queue();
                    return;
                } else {
                    ticket = new WhitelistApplicationTicket(modalInteraction.getGuild(),
                            modalInteraction.getUser(), minecraftName);
                }
            } else if (ticketType.equals(TicketType.SERVER_SUPPORT) || ticketType.equals(TicketType.DISCORD_SUPPORT)
                    || ticketType.equals(TicketType.BUGREPORT)) {
                String description = modalInteraction.getValue("body").getAsString();

                if (ticketType.equals(TicketType.SERVER_SUPPORT)) {
                    ticket = new ServerSupportTicket(modalInteraction.getGuild(),
                            modalInteraction.getUser(), description);
                } else if (ticketType.equals(TicketType.DISCORD_SUPPORT)) {
                    ticket = new DiscordSupportTicket(modalInteraction.getGuild(),
                            modalInteraction.getUser(), description);
                } else if (ticketType.equals(TicketType.BUGREPORT)) {
                    ticket = new BugReportTicket(modalInteraction.getGuild(),
                            modalInteraction.getUser(), description);
                }
            }

            if (ticket != null) {
                final Ticket finalTicket = ticket;

                ticket.createTicketChannel(event.getGuild()).thenAcceptAsync(result -> {
                    if (result.equals(TicketCreateResult.SUCCESS)) {
                        finalTicket.afterOpen();

                        StringBuilder message = new StringBuilder();
                        message.append("Dein \"");
                        message.append(this.ticketType.getName());
                        message.append("\"-Ticket wurde erfolgreich erstellt! ");

                        message.append(finalTicket.getChannel().getAsMention());

                        hook.editOriginal(message.toString()).queue();
                        return;
                    } else if (result.equals(TicketCreateResult.ALREADY_EXISTS)) {
                        hook.editOriginal("Du hast bereits ein Ticket mit diesem Typen offen!").queue();
                        return;
                    } else {
                        hook.editOriginal("Es ist ein Fehler aufgetreten!").queue();
                        System.err.println("Error while creating ticket: " + result);
                        return;
                    }
                });
            } else {
                hook.editOriginal("Es konnte kein Ticket mit dem angegebenen Ticket-Typen erstellt werden!")
                        .queue();
                return;
            }
        });
    }

    public static class WhitelistTicketModal extends TicketModal {
        public WhitelistTicketModal() {
            super(TicketType.WHITELIST);
        }

        @Override
        public String getCustomId() {
            return TicketType.WHITELIST.name();
        }
    }

    public static class ServerSupportTicketModal extends TicketModal {
        public ServerSupportTicketModal() {
            super(TicketType.SERVER_SUPPORT);
        }

        @Override
        public String getCustomId() {
            return TicketType.SERVER_SUPPORT.name();
        }
    }

    public static class DiscordSupportTicketModal extends TicketModal {
        public DiscordSupportTicketModal() {
            super(TicketType.DISCORD_SUPPORT);
        }

        @Override
        public String getCustomId() {
            return TicketType.DISCORD_SUPPORT.name();
        }
    }

    public static class BugReportTicketModal extends TicketModal {
        public BugReportTicketModal() {
            super(TicketType.BUGREPORT);
        }

        @Override
        public String getCustomId() {
            return TicketType.BUGREPORT.name();
        }
    }

}
