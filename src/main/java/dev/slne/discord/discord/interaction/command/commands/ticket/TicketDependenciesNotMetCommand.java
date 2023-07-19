package dev.slne.discord.discord.interaction.command.commands.ticket;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import dev.slne.discord.Launcher;
import dev.slne.discord.discord.guild.permission.DiscordPermission;
import dev.slne.discord.discord.interaction.command.commands.TicketCommand;
import dev.slne.discord.ticket.result.TicketCloseResult;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class TicketDependenciesNotMetCommand extends TicketCommand {

    /**
     * Creates a new {@link TicketCloseCommand}.
     */
    public TicketDependenciesNotMetCommand() {
        super("no-dependencies", "Closes a ticket whilst telling the user that they do not have met the dependencies.");
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
    public @Nonnull DiscordPermission getPermission() {
        return DiscordPermission.USE_COMMAND_TICKET_CLOSE;
    }

    @Override
    public void execute(SlashCommandInteractionEvent interaction) {
        User closer = interaction.getUser();
        String reason = "Du erfüllst nicht die Voraussetzungen. Bitte lies dir diese genauer durch, bevor du ein neues Ticket eröffnest.";

        interaction.reply("Schließe Ticket...").setEphemeral(true)
                .queue(deferedReply -> getTicket().close(closer, reason).whenComplete(result -> {
                    if (result != TicketCloseResult.SUCCESS) {
                        deferedReply.editOriginal("Fehler beim Schließen des Tickets.").queue();
                        Launcher.getLogger(getClass()).error("Error while closing ticket: {}", result.name());
                    }
                }, throwable -> {
                    deferedReply.editOriginal("Fehler beim Schließen des Tickets.").queue();
                    Launcher.getLogger(getClass()).error("Error while closing ticket", throwable);
                }));
    }

}
