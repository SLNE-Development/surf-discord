package dev.slne.discord.exception.ticket;

import dev.slne.discord.exception.DiscordException;

public class UnableToGetTicketNameException extends DiscordException {

  public UnableToGetTicketNameException(String message) {
    super(message);
  }

  public UnableToGetTicketNameException(String message, Throwable cause) {
    super(message, cause);
  }
}
