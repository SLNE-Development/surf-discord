package dev.slne.discord.ticket.commands;

import java.util.ArrayList;
import java.util.List;

import dev.slne.discord.interaction.command.DiscordCommand;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.result.TicketCloseResult;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class TicketCloseCommand extends DiscordCommand {

    public TicketCloseCommand() {
        super("close", "Closes a ticket.");
    }

    @Override
    public List<SubcommandData> getSubCommands() {
        return new ArrayList<>();
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();

        options.add(new OptionData(OptionType.STRING, "reason", "The reason for closing the ticket.", true));

        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent interaction) {
        User closer = interaction.getUser();
        String reason = interaction.getOption("reason").getAsString();

        if (!(interaction.getChannel() instanceof TextChannel)) {
            interaction.reply("Es können nur Text-Channel geschlossen werden.").setEphemeral(true).queue();
            return;
        }

        TextChannel channel = (TextChannel) interaction.getChannel();

        interaction.deferReply(true).queue(deferedReply -> {
            Ticket.getTicketByChannel(channel).thenAcceptAsync((ticket) -> {
                if (ticket == null) {
                    deferedReply.editOriginal("Dieser Kanal ist kein Ticket.").queue();
                    return;
                }

                ticket.closeTicket(closer, reason).thenAcceptAsync(result -> {
                    if (result == TicketCloseResult.SUCCESS) {
                        deferedReply.editOriginal("Ticket erfolgreich geschlossen.").queue();
                    } else {
                        deferedReply.editOriginal("Fehler beim Schließen des Tickets.").queue();
                    }
                }).exceptionally(failure -> {
                    deferedReply.editOriginal("Fehler beim Schließen des Tickets.").queue();
                    System.err.println("Error while closing ticket: " + failure.getMessage());
                    failure.printStackTrace();
                    return null;
                });
            });
        }, failure -> {
            System.err.println("Error while closing ticket: " + failure.getMessage());
            failure.printStackTrace();
        });
    }

}
