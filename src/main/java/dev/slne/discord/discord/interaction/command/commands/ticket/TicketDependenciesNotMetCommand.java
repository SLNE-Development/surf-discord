package dev.slne.discord.discord.interaction.command.commands.ticket;

import dev.slne.discord.annotation.DiscordCommandMeta;
import dev.slne.discord.discord.interaction.command.commands.TicketCommand;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.guild.permission.CommandPermission;
import dev.slne.discord.message.RawMessages;
import dev.slne.discord.spring.service.ticket.TicketService;
import dev.slne.discord.ticket.TicketCreator;
import dev.slne.discord.ticket.result.TicketCloseResult;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
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

  private final TicketCreator ticketCreator;

  @Autowired
  public TicketDependenciesNotMetCommand(TicketService ticketService, TicketCreator ticketCreator) {
    super(ticketService);
    this.ticketCreator = ticketCreator;
  }

  @Override
  public void internalExecute(SlashCommandInteractionEvent interaction, InteractionHook hook)
      throws CommandException {
    User closer = interaction.getUser();
    closeTicket(closer);
  }

  @Async
  protected void closeTicket(User closer) throws CommandException {
    final TicketCloseResult closeResult = ticketCreator.closeTicket(
        getTicket(),
        closer,
        RawMessages.get("interaction.command.ticket.dependencies-not-met.close-reason")
    ).join();

    if (closeResult != TicketCloseResult.SUCCESS) {
      throw CommandException.ticketClose(closeResult);
    }
  }
}
