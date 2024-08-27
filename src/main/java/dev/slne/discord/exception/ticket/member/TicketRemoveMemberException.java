package dev.slne.discord.exception.ticket.member;

import dev.slne.discord.exception.DiscordException;
import java.io.Serial;

public class TicketRemoveMemberException extends TicketMemberException {

  @Serial
  private static final long serialVersionUID = -196685204706749789L;

  public TicketRemoveMemberException(String message) {
    super(message);
  }

  public TicketRemoveMemberException(String message, Throwable cause) {
    super(message, cause);
  }

  public TicketRemoveMemberException(DiscordException exception) {
    super(exception);
  }
}
