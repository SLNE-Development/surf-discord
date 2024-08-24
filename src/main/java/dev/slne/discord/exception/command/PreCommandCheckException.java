package dev.slne.discord.exception.command;

import dev.slne.discord.exception.DiscordException;
import java.io.Serial;

public class PreCommandCheckException extends DiscordException {

  @Serial
  private static final long serialVersionUID = 129434452849941329L;

  public PreCommandCheckException(String message) {
    super(message);
  }

  public PreCommandCheckException(String message, Throwable cause) {
    super(message, cause);
  }
}
