package dev.slne.discord.exception.command;

import dev.slne.discord.exception.DiscordException;
import dev.slne.discord.message.RawMessages;
import dev.slne.discord.ticket.result.TicketCloseResult;
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

  public static CommandException noGuild() {
    return new CommandException(RawMessages.get("error.no-guild"));
  }

  public static CommandException serverNotRegistered() {
    return new CommandException(RawMessages.get("error.server-not-registered"));
  }

  public static CommandException noTextChannel() {
    return new CommandException(RawMessages.get("error.no-text-channel"));
  }

  public static CommandException ticketClose(TicketCloseResult closeResult) {
    return new CommandException(RawMessages.get("error.ticket.close"),
        new IllegalStateException("TicketCloseResult: " + closeResult));
  }

  public static CommandException ticketAddBot() {
    return new CommandException(RawMessages.get("error.ticket.add.bot"));
  }

  public static CommandException ticketAddMember(Throwable cause) {
    return new CommandException(RawMessages.get("error.ticket.add.member"), cause);
  }

  public static CommandException ticketAddMemberAlreadyInTicket() {
    return new CommandException(RawMessages.get("error.ticket.add.member.already-in-ticket"));
  }
}
