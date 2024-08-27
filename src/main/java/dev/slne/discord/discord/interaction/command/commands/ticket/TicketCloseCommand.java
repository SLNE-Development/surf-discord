package dev.slne.discord.discord.interaction.command.commands.ticket;

import dev.slne.discord.annotation.DiscordCommandMeta;
import dev.slne.discord.discord.interaction.command.commands.TicketCommand;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.exception.command.CommandExceptions;
import dev.slne.discord.guild.permission.CommandPermission;
import dev.slne.discord.message.RawMessages;
import dev.slne.discord.spring.service.ticket.TicketService;
import dev.slne.discord.ticket.TicketCreator;
import dev.slne.discord.ticket.result.TicketCloseResult;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
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

  public static final String REASON_OPTION = "reason";
  private final TicketCreator ticketCreator;

  @Autowired
  public TicketCloseCommand(TicketService ticketService, TicketCreator ticketCreator) {
    super(ticketService);
    this.ticketCreator = ticketCreator;
  }

  @Override
  public @Nonnull List<OptionData> getOptions() {
    return List.of(
        new OptionData(
            OptionType.STRING,
            REASON_OPTION,
            RawMessages.get("interaction.command.ticket.close.arg.reason"),
            true
        )
    );
  }

  @Override
  public void internalExecute(@NotNull SlashCommandInteractionEvent interaction,
      @NotNull InteractionHook hook)
      throws CommandException {
    final User closer = interaction.getUser();
    final String reason = getStringOrThrow(interaction, REASON_OPTION,
        "You must provide a reason.");

    hook.editOriginal(RawMessages.get("interaction.command.ticket.close.closing")).queue();
    closeTicket(closer, reason);
  }

  @Async
  protected void closeTicket(
      User closer,
      String closeReason
  ) throws CommandException {
    final TicketCloseResult closeResult = ticketCreator.closeTicket(getTicket(), closer,
        closeReason).join();

    if (closeResult != TicketCloseResult.SUCCESS) {
      throw CommandExceptions.TICKET_CLOSE.create(closeResult);
    }
  }
}
