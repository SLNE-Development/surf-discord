package dev.slne.discord.exception.command;

import dev.slne.discord.exception.DiscordException;
import java.io.Serial;
import org.intellij.lang.annotations.Language;

public class CommandException extends DiscordException {

  @Serial
  private static final long serialVersionUID = -2363935215977387153L;

  public CommandException(@Language("markdown") String message) {
    super(message);
  }

  public CommandException(@Language("markdown") String message, Throwable cause) {
    super(message, cause);
  }
}
