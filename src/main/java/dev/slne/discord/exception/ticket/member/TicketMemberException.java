package dev.slne.discord.exception.ticket.member;

import dev.slne.discord.exception.DiscordException;

public abstract class TicketMemberException extends DiscordException {

  public TicketMemberException(String message) {
    super(message);
  }

  public TicketMemberException(String message, Throwable cause) {
    super(message, cause);
  }
}
