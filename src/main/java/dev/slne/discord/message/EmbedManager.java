package dev.slne.discord.message;

import dev.slne.discord.exception.ticket.UnableToGetTicketNameException;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketChannelHelper;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@ExtensionMethod({TimeFormatter.class})
public class EmbedManager {

  private final TicketChannelHelper ticketChannelHelper;

  public EmbedManager(TicketChannelHelper ticketChannelHelper) {
    this.ticketChannelHelper = ticketChannelHelper;
  }

  @Async
  public CompletableFuture<MessageEmbed> buildTicketClosedEmbed(Ticket ticket) {
    try {
      final String ticketName = ticketChannelHelper.getTicketName(ticket).join();
      final Optional<User> author = ticket.getTicketAuthorNow();
      final Optional<User> closedBy = ticket.getClosedByNow();
      final String closeReason = ticket.getCloseReasonOrDefault();

      final StringBuilder descriptionBuilder = new StringBuilder("Ein Ticket wurde ");
      closedBy.ifPresent(user -> descriptionBuilder.append("von ").append(user.getAsMention()));
      descriptionBuilder.append("geschlossen.\n\n")
          .append("Grund: ")
          .append(closeReason);

      return CompletableFuture.completedFuture(new EmbedBuilder()
          .setTitle("Ticket \"%s\" geschlossen".formatted(ticketName))
          .setDescription(descriptionBuilder.toString())
          .setColor(EmbedColors.TICKET_CLOSED)
          .addField("Ticket-Id", toStringOrUnknown(ticket.getTicketId()), true)
          .addField("Ticket-Type", toStringOrUnknown(ticket.getTicketTypeString()), true)
          .addField("Ticket-Author", toStringOrUnknown(author.map(IMentionable::getAsMention)),
              true)
          .addField("Ticket-Eröffnungszeit", ticket.getOpenedAt().formatEuropeBerlin(), true)
          .addField("Ticket-Schließzeit", ticket.getClosedAt().formatEuropeBerlin(), true)
          .addField("Ticket-Dauer",
              ticket.getOpenedAt().formatEuropeBerlinDuration(ticket.getClosedAt()), true)
          .build());
    } catch (UnableToGetTicketNameException e) {
      return CompletableFuture.completedFuture(null);
    }
  }

  private String toStringOrUnknown(@Nullable Object object) {
    return object == null ? "Unbekannt" : object.toString();
  }

  private String toStringOrUnknown(@NotNull Optional<?> optional) {
    return optional.map(Object::toString).orElse("Unbekannt");
  }
}
