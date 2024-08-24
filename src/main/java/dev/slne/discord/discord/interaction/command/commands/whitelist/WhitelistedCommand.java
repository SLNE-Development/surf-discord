package dev.slne.discord.discord.interaction.command.commands.whitelist;

import dev.slne.data.api.DataApi;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.guild.permission.CommandPermission;
import dev.slne.discord.discord.interaction.command.commands.TicketCommand;
import dev.slne.discord.spring.annotation.DiscordCommandMeta;
import dev.slne.discord.spring.service.ticket.TicketService;
import dev.slne.discord.ticket.result.TicketCloseResult;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import org.intellij.lang.annotations.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

/**
 * The type Whitelisted command.
 */
@DiscordCommandMeta(
		name = "whitelisted",
		description = "Schließt ein Ticket mit der Begründung, dass der Nutzer whitelisted wurde.",
		permission = CommandPermission.WHITELISTED
)
public class WhitelistedCommand extends TicketCommand {

	@Language("Markdown")
	private static final String CLOSE_REASON = "Du befindest dich nun auf der Whitelist.";

	@Autowired
	public WhitelistedCommand(TicketService ticketService) {
		super(ticketService);
	}

	@Override
	public void internalExecute(SlashCommandInteractionEvent interaction, InteractionHook hook)
			throws CommandException {
		final User closer = interaction.getUser();

		hook.editOriginal("Schließe Ticket...").queue();
		closeTicket(closer);
	}

	@Async
	protected void closeTicket(User closer) throws CommandException {
		final TicketCloseResult closeResult = getTicket().close(closer, CLOSE_REASON).join();

		if (closeResult != TicketCloseResult.SUCCESS) {
			throw new CommandException("Fehler beim Schließen des Tickets.", new IllegalStateException("Error while closing ticket: " + closeResult.name()));
		}
	}
}
