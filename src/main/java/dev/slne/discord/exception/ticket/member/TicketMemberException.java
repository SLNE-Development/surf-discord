package dev.slne.discord.exception.ticket.member;

import dev.slne.discord.exception.DiscordException;
import java.io.Serial;

public abstract class TicketMemberException extends DiscordException {

  @Serial
  private static final long serialVersionUID = 8277057907153655874L;

  public TicketMemberException(String message) {
    super(message);
  }

  public TicketMemberException(String message, Throwable cause) {
    super(message, cause);
  }

  public TicketMemberException(DiscordException exception) {
    super(exception);
  }
}
