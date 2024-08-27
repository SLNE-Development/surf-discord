package dev.slne.discord.exception.ticket;

import dev.slne.discord.exception.DiscordException;
import java.io.Serial;

public class UnableToGetTicketNameException extends DiscordException {

  @Serial
  private static final long serialVersionUID = -575479591760370835L;

  public UnableToGetTicketNameException(String message) {
    super(message);
  }

  public UnableToGetTicketNameException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnableToGetTicketNameException(DiscordException exception) {
    super(exception);
  }
}
