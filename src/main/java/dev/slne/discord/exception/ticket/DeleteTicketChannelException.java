package dev.slne.discord.exception.ticket;

import dev.slne.discord.exception.DiscordException;

public class DeleteTicketChannelException extends DiscordException {

  public DeleteTicketChannelException(String message) {
    super(message);
  }

  public DeleteTicketChannelException(String message, Throwable cause) {
    super(message, cause);
  }
}
