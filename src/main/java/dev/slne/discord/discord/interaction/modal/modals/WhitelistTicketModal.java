package dev.slne.discord.discord.interaction.modal.modals;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import dev.slne.discord.Launcher;
import dev.slne.discord.discord.interaction.modal.DiscordModal;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketType;
import dev.slne.discord.ticket.result.TicketCreateResult;
import dev.slne.discord.ticket.tickets.WhitelistApplicationTicket;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

public class WhitelistTicketModal extends DiscordModal {

    /**
     * Constructor for a whitelist ticket modal
     */
    public WhitelistTicketModal() {
        super(TicketType.WHITELIST.getName() + " Ticket erstellen");
    }

    @Override
    public void fillComponents() {
        TextInput minecraftNameInput = TextInput.create("minecraft-name", "Minecraft-Name", TextInputStyle.SHORT)
                .setRequired(true).build();

        TextInput discordTwitchVerified = TextInput.create("discord-twitch-verified",
                "Twitch-Account verbunden?", TextInputStyle.SHORT)
                .setPlaceholder("Ja").setValue("Nein").setRequired(true).build();

        components.add(minecraftNameInput);
        components.add(discordTwitchVerified);
    }

    @Override
    @SuppressWarnings({ "java:S3776", "java:S1192" })
    public void execute(ModalInteractionEvent event) {
        ModalInteraction modalInteraction = event.getInteraction();

        TicketType ticketType = TicketType.getByName(modalInteraction.getModalId());
        if (ticketType == null) {
            event.reply("Es ist ein Fehler beim abrufen des Ticket-Typs aufgetreten.").setEphemeral(true).queue();
            return;
        }

        InteractionHook hook = modalInteraction.deferReply(true).complete();
        if (!ticketType.equals(TicketType.WHITELIST)) {
            return;
        }

        ModalMapping minecraftNameValue = modalInteraction.getValue("minecraft-name");
        if (minecraftNameValue == null) {
            hook.editOriginal("Es ist ein Fehler aufgetreten!").queue();
            Launcher.getLogger().logError("Error while creating ticket: minecraftNameOption is null");
            return;
        }
        String minecraftName = minecraftNameValue.getAsString();

        ModalMapping discordTwitchVerifiedValue = modalInteraction.getValue("discord-twitch-verified");
        if (discordTwitchVerifiedValue == null) {
            hook.editOriginal("Es ist ein Fehler aufgetreten!").queue();
            Launcher.getLogger().logError("Error while creating ticket: discordTwitchVerifiedOption is null");
            return;
        }
        String discordTwitchVerifiedString = discordTwitchVerifiedValue.getAsString();

        List<String> validInput = Arrays.asList("ja", "yes", "j", "y");
        boolean discordTwitchVerified = validInput.contains(discordTwitchVerifiedString.toLowerCase());

        if (!discordTwitchVerified) {
            hook.editOriginal(
                    "Dein Twitch-Account muss mit deinem Discord-Account verknüpft sein, um auf dem Server spielen zu dürfen.")
                    .queue();
            return;
        }
        Ticket ticket = new WhitelistApplicationTicket(modalInteraction.getGuild(),
                modalInteraction.getUser());

        ticket.openFromButton().whenComplete(result -> {
            if (result.equals(TicketCreateResult.SUCCESS)) {
                StringBuilder message = new StringBuilder();
                message.append("Dein \"");
                message.append(TicketType.WHITELIST.getName());
                message.append("\"-Ticket wurde erfolgreich erstellt! ");

                Optional<TextChannel> channelOptional = ticket.getChannel();
                if (channelOptional.isPresent()) {
                    message.append(channelOptional.get().getAsMention());
                }

                String messageString = message.toString();
                if (messageString != null) {
                    hook.editOriginal(messageString).queue();
                }

                if (minecraftName != null && channelOptional.isPresent()) {
                    channelOptional.get().sendMessage("Minecraft-Name: " + minecraftName).queue();
                }

                return;
            } else if (result.equals(TicketCreateResult.ALREADY_EXISTS)) {
                hook.editOriginal(
                        "Du hast bereits ein Ticket mit dem angegeben Typ geöffnet. Sollte dies nicht der Fall sein, wende dich per Ping an @notammo.")
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
        }, failure -> {
            hook.editOriginal("Es ist ein Fehler aufgetreten!").queue();
            Launcher.getLogger().logError("Error while creating ticket: ", failure);
        });
    }

    @Override
    public @Nonnull String getCustomId() {
        return TicketType.WHITELIST.name() + "";
    }

}
