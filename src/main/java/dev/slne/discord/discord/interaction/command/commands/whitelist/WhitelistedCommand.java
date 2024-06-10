package dev.slne.discord.discord.interaction.command.commands.whitelist;

import dev.slne.data.api.DataApi;
import dev.slne.discord.discord.guild.permission.CommandPermission;
import dev.slne.discord.discord.interaction.command.commands.TicketCommand;
import dev.slne.discord.ticket.result.TicketCloseResult;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Whitelisted command.
 */
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
	public @Nonnull CommandPermission getPermission() {
		return CommandPermission.WHITELISTED;
	}

	@Override
	public void execute(SlashCommandInteractionEvent interaction) {
		User closer = interaction.getUser();
		String reason = "Du befindest dich nun auf der Whitelist.";

		interaction.reply("Schließe Ticket...").setEphemeral(true)
				   .queue(deferedReply -> getTicket().close(closer, reason).thenAcceptAsync(result -> {
					   if (result != TicketCloseResult.SUCCESS) {
						   deferedReply.editOriginal("Fehler beim Schließen des Tickets.").queue();
						   DataApi.getDataInstance()
								  .logError(getClass(), "Error while closing ticket: " + result.name());
					   }
				   }).exceptionally(exception -> {
					   deferedReply.editOriginal("Fehler beim Schließen des Tickets.").queue();
					   DataApi.getDataInstance().logError(getClass(), "Error while closing ticket", exception);

					   return null;
				   }));
	}
}
