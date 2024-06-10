package dev.slne.discord.discord.interaction.command.commands.ticket;

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
 * The type Ticket dependencies not met command.
 */
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
	public @Nonnull CommandPermission getPermission() {
		return CommandPermission.TICKET_CLOSE;
	}

	@Override
	public void execute(SlashCommandInteractionEvent interaction) {
		User closer = interaction.getUser();
		String reason =
				"Du erfüllst nicht die Voraussetzungen. Bitte lies dir diese genauer durch, bevor du ein neues Ticket eröffnest.";

		interaction.reply("Schließe Ticket...").setEphemeral(true)
				   .queue(deferredReply -> getTicket().close(closer, reason).thenAcceptAsync(result -> {
					   if (result != TicketCloseResult.SUCCESS) {
						   deferredReply.editOriginal("Fehler beim Schließen des Tickets.").queue();
						   DataApi.getDataInstance()
								  .logError(getClass(), "Error while closing ticket: " + result.name());
					   }
				   }).exceptionally(exception -> {
					   deferredReply.editOriginal("Fehler beim Schließen des Tickets.").queue();
					   DataApi.getDataInstance().logError(getClass(), "Error while closing ticket", exception);

					   return null;
				   }));
	}

}
