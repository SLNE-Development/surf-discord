package dev.slne.discord.exception.command;

import java.io.Serial;

public class PreTicketCommandException extends PreCommandCheckException{

  @Serial
  private static final long serialVersionUID = -1353290082500326289L;

  public PreTicketCommandException(String message) {
    super(message);
  }

  public PreTicketCommandException(String message, Throwable cause) {
    super(message, cause);
  }
}
