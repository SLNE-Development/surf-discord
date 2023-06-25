package dev.slne.discord.discord.interaction.command.commands.whitelist;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import dev.slne.discord.Launcher;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketRepository;
import dev.slne.discord.ticket.result.TicketCloseResult;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class WhitelistedCommand extends DiscordCommand {

    /**
     * Creates a new {@link WhitelistedCommand}.
     */
    public WhitelistedCommand() {
        super("whitelisted", "Schließt ein Ticket mit der Begründung, dass der Nutzer whitelisted wurde.");
    }

    @Override
    public @Nonnull List<SubcommandData> getSubCommands() {
        return new ArrayList<>();
    }

    @Override
    public @Nonnull List<OptionData> getOptions() {
        return new ArrayList<>();
    }

    @Override
    public void execute(SlashCommandInteractionEvent interaction) {
        User closer = interaction.getUser();
        String reason = "Du befindest dich nun auf der Whitelist.";

        if (!(interaction.getChannel() instanceof TextChannel)) {
            interaction.reply("Es können nur Text-Channel geschlossen werden.").setEphemeral(true).queue();
            return;
        }

        TextChannel channel = (TextChannel) interaction.getChannel();
        Optional<Ticket> ticketOptional = TicketRepository.getTicketByChannel(channel.getId());

        interaction.reply("Schließe Ticket...")
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
