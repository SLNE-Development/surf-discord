package dev.slne.discord.discord.interaction.command.commands.whitelist;

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

public class WhitelistedCommand extends TicketCommand {

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
    public @Nonnull DiscordPermission getPermission() {
        return DiscordPermission.USE_COMMAND_WHITELISTED;
    }

    @Override
    public void execute(SlashCommandInteractionEvent interaction) {
        User closer = interaction.getUser();
        String reason = "Du befindest dich nun auf der Whitelist.";

        interaction.reply("Schließe Ticket...").setEphemeral(true)
                .queue(deferedReply -> getTicket().close(closer, reason).whenComplete(result -> {
                    if (result != TicketCloseResult.SUCCESS) {
                        deferedReply.editOriginal("Fehler beim Schließen des Tickets.").queue();
                    }
                }, throwable -> {
                    deferedReply.editOriginal("Fehler beim Schließen des Tickets.").queue();
                    Launcher.getLogger(getClass()).error("Error while closing ticket", throwable);
                }));
    }
}
