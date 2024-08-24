package dev.slne.discord.discord.interaction.command.commands;

import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.exception.command.PreCommandCheckException;
import dev.slne.discord.exception.command.PreTicketCommandException;
import dev.slne.discord.spring.service.ticket.TicketService;
import dev.slne.discord.ticket.Ticket;
import java.util.concurrent.CompletableFuture;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;

/**
 * The type Ticket command.
 */
@Getter
public abstract class TicketCommand extends DiscordCommand {

  private final TicketService ticketService;
  private Ticket ticket;
  private TextChannel channel;

  public TicketCommand(TicketService ticketService) {
    this.ticketService = ticketService;
  }

  @Async
  @Override
  @OverridingMethodsMustInvokeSuper
  protected CompletableFuture<Boolean> performAdditionalChecks(
      User user,
      Guild guild,
      @NotNull SlashCommandInteractionEvent interaction
  ) throws PreTicketCommandException {
    if (!(interaction.getChannel() instanceof TextChannel textChannel)) {
      throw new PreTicketCommandException(
          "Dieser Befehl kann nur in einem Ticketkanal verwendet werden.");
    }

    this.ticket = ticketService.getTicketByChannelId(textChannel.getId())
        .orElseThrow(() ->
            new PreTicketCommandException(
                "Dieser Befehl kann nur in einem Ticket verwendet werden."));
    this.channel = textChannel;

    return CompletableFuture.completedFuture(true);
  }
}
