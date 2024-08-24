package dev.slne.discord.discord.interaction.command.commands.ticket;

import dev.slne.discord.discord.interaction.command.commands.TicketCommand;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.guild.permission.CommandPermission;
import dev.slne.discord.spring.annotation.DiscordCommandMeta;
import dev.slne.discord.spring.service.ticket.TicketService;
import dev.slne.discord.ticket.result.TicketCloseResult;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

/**
 * The type Ticket close command.
 */
@DiscordCommandMeta(name = "close", description = "Closes a ticket.", permission = CommandPermission.TICKET_CLOSE)
public class TicketCloseCommand extends TicketCommand {

  @Autowired
  public TicketCloseCommand(TicketService ticketService) {
    super(ticketService);
  }

  @Override
  public @Nonnull List<OptionData> getOptions() {
    return List.of(
        new OptionData(OptionType.STRING, "reason", "The reason for closing the ticket.", true)
    );
  }

  @Override
  public void internalExecute(@NotNull SlashCommandInteractionEvent interaction,
      @NotNull InteractionHook hook)
      throws CommandException {
    final User closer = interaction.getUser();
    final OptionMapping reasonOption = interaction.getOption("reason");
    final String reason = reasonOption == null ? "No reason provided." : reasonOption.getAsString();

    hook.editOriginal("Schließe Ticket...").queue();
    closeTicket(closer, reason);
  }

  @Async
  protected void closeTicket(
      User closer,
      String closeReason
  ) throws CommandException {
    final TicketCloseResult closeResult = getTicket().close(closer, closeReason).join();

    if (closeResult != TicketCloseResult.SUCCESS) {
      throw new CommandException("Es ist ein Fehler beim Schließen des Tickets aufgetreten.",
          new Throwable(closeResult.name()));
    }
  }
}
