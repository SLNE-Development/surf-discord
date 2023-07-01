package dev.slne.discord.discord.interaction.command.commands.ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import dev.slne.discord.Launcher;
import dev.slne.discord.discord.guild.permission.DiscordPermission;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketRepository;
import dev.slne.discord.ticket.result.TicketCloseResult;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class TicketCloseCommand extends DiscordCommand {

    /**
     * Creates a new {@link TicketCloseCommand}.
     */
    public TicketCloseCommand() {
        super("close", "Closes a ticket.");
    }

    @Override
    public @Nonnull List<SubcommandData> getSubCommands() {
        return new ArrayList<>();
    }

    @Override
    public @Nonnull List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();

        options.add(new OptionData(OptionType.STRING, "reason", "The reason for closing the ticket.", true));

        return options;
    }

    @Override
    public @Nonnull DiscordPermission getPermission() {
        return DiscordPermission.USE_COMMAND_TICKET_CLOSE;
    }

    @Override
    public void execute(SlashCommandInteractionEvent interaction) {
        User closer = interaction.getUser();
        OptionMapping reasonOption = interaction.getOption("reason");
        String reason = reasonOption == null ? "No reason provided." : reasonOption.getAsString();

        if (!(interaction.getChannel() instanceof TextChannel)) {
            interaction.reply("Es können nur Text-Channel geschlossen werden.").setEphemeral(true).queue();
            return;
        }

        TextChannel channel = (TextChannel) interaction.getChannel();
        Optional<Ticket> ticketOptional = TicketRepository.getTicketByChannel(channel.getId());

        interaction.reply("Schließe Ticket...").setEphemeral(true)
                .queue(deferedReply -> {
                    if (ticketOptional.isEmpty()) {
                        deferedReply.editOriginal("Dieser Kanal ist kein Ticket.").queue();
                        return;
                    }

                    Ticket ticket = ticketOptional.get();

                    ticket.close(closer, reason).whenComplete(result -> {
                        if (result != TicketCloseResult.SUCCESS) {
                            deferedReply.editOriginal("Fehler beim Schließen des Tickets.").queue();
                        }
                    }, throwable -> {
                        deferedReply.editOriginal("Fehler beim Schließen des Tickets.").queue();
                        Launcher.getLogger().logError("Error while closing ticket: " + throwable.getMessage());
                        throwable.printStackTrace();
                    });
                });
    }

}
