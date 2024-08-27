package dev.slne.discord.discord.interaction.command.commands.whitelist;

import dev.slne.discord.annotation.DiscordCommandMeta;
import dev.slne.discord.discord.interaction.command.commands.TicketCommand;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.exception.command.CommandExceptions;
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
 * The type Whitelisted command.
 */
@DiscordCommandMeta(
    name = "whitelisted",
    description = "Schließt ein Ticket mit der Begründung, dass der Nutzer whitelisted wurde.",
    permission = CommandPermission.WHITELISTED
)
public class WhitelistedCommand extends TicketCommand {

  private final TicketCreator ticketCreator;

  @Autowired
  public WhitelistedCommand(TicketService ticketService, TicketCreator ticketCreator) {
    super(ticketService);
    this.ticketCreator = ticketCreator;
  }

  @Override
  public void internalExecute(SlashCommandInteractionEvent interaction, InteractionHook hook)
      throws CommandException {
    final User closer = interaction.getUser();

    hook.editOriginal(RawMessages.get("interaction.command.ticket.close.closing")).queue();
    closeTicket(closer);
  }

  @Async
  protected void closeTicket(User closer) throws CommandException {
    final TicketCloseResult closeResult = ticketCreator.closeTicket(getTicket(), closer,
        RawMessages.get("interaction.command.ticket.whitelisted.close-reason")).join();

    if (closeResult != TicketCloseResult.SUCCESS) {
      throw CommandExceptions.TICKET_CLOSE.create(closeResult);
    }
  }
}
