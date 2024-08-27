package dev.slne.discord.exception.ticket;

import dev.slne.discord.exception.DiscordException;
import java.io.Serial;

public class DeleteTicketChannelException extends DiscordException {

  @Serial
  private static final long serialVersionUID = -2980471014795318817L;

  public DeleteTicketChannelException(String message) {
    super(message);
  }

  public DeleteTicketChannelException(String message, Throwable cause) {
    super(message, cause);
  }

  public DeleteTicketChannelException(DiscordException exception) {
    super(exception);
  }
}
