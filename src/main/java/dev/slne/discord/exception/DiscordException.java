package dev.slne.discord.exception;

import java.io.Serial;

public abstract class DiscordException extends Exception {

  @Serial
  private static final long serialVersionUID = 7077818795975848866L;

  public DiscordException(String message) {
    super(message);
  }

  public DiscordException(String message, Throwable cause) {
    super(message, cause);
  }

  public DiscordException(DiscordException exception) {
    super(exception.getMessage(), exception);
  }
}
