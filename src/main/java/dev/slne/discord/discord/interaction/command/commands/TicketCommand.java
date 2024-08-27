package dev.slne.discord.discord.interaction.command.commands;

import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.exception.command.pre.PreTicketCommandException;
import dev.slne.discord.message.RawMessages;
import dev.slne.discord.spring.service.ticket.TicketService;
import dev.slne.discord.ticket.Ticket;
import java.util.concurrent.CompletableFuture;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
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
      @NotNull SlashCommandInteractionEvent interaction,
      InteractionHook hook
  ) throws PreTicketCommandException {
    final TextChannel textChannel;
    try {
      textChannel = getTextChannelOrThrow(interaction);
    } catch (CommandException e) {
      throw new PreTicketCommandException(e);
    }

    this.ticket = ticketService.getTicketByChannelId(textChannel.getId()).orElseThrow(
        () -> new PreTicketCommandException(RawMessages.get("error.ticket.no-ticket-channel")));
    this.channel = textChannel;

    return CompletableFuture.completedFuture(true);
  }
}
