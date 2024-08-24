package dev.slne.discord.discord.interaction.command.commands.ticket;

import dev.slne.discord.discord.interaction.command.commands.TicketCommand;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.guild.permission.CommandPermission;
import dev.slne.discord.spring.annotation.DiscordCommandMeta;
import dev.slne.discord.spring.service.ticket.TicketService;
import dev.slne.discord.ticket.result.TicketCloseResult;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.intellij.lang.annotations.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

/**
 * The type Ticket dependencies not met command.
 */
@DiscordCommandMeta(
    name = "no-dependencies",
    description = "Closes a ticket whilst telling the user that they do not have met the dependencies.",
    permission = CommandPermission.TICKET_CLOSE
)
public class TicketDependenciesNotMetCommand extends TicketCommand {

  @Language("markdown")
  private static final String CLOSE_REASON = "Du erfüllst nicht die Voraussetzungen. Bitte lies dir diese genauer durch, bevor du ein neues Ticket eröffnest.";

  @Autowired
  public TicketDependenciesNotMetCommand(TicketService ticketService) {
    super(ticketService);
  }

  @Override
  public void internalExecute(SlashCommandInteractionEvent interaction, InteractionHook hook)
      throws CommandException {
    User closer = interaction.getUser();
    closeTicket(closer);
  }

  @Async
  protected void closeTicket(User closer) throws CommandException {
    final TicketCloseResult closeResult = getTicket().close(closer, CLOSE_REASON).join();

    if (closeResult != TicketCloseResult.SUCCESS) {
      throw new CommandException("Fehler beim Schließen des Tickets.",
          new IllegalStateException("TicketCloseResult: " + closeResult));
    }
  }
}
