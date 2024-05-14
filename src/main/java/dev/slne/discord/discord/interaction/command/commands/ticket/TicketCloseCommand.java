package dev.slne.discord.discord.interaction.command.commands.ticket;

import dev.slne.data.api.DataApi;
import dev.slne.discord.discord.guild.permission.DiscordPermission;
import dev.slne.discord.discord.interaction.command.commands.TicketCommand;
import dev.slne.discord.ticket.result.TicketCloseResult;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Ticket close command.
 */
public class TicketCloseCommand extends TicketCommand {

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
