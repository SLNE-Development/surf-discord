package dev.slne.discord.exception.command;

import dev.slne.discord.message.RawMessages;
import dev.slne.discord.ticket.result.TicketCloseResult;
import dev.slne.discord.util.ExceptionFactory.CommandExceptionFactory;
import dev.slne.discord.util.ExceptionFactory.ExceptionFactory1.CommandExceptionFactory1;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CommandExceptions {

  // @formatter:off
  public final CommandExceptionFactory GENERIC = () -> new CommandException(RawMessages.get("error.generic"));
  public final CommandExceptionFactory NO_GUILD = () -> new CommandException(RawMessages.get("error.no-guild"));
  public final CommandExceptionFactory SERVER_NOT_REGISTERED = () -> new CommandException(RawMessages.get("error.server-not-registered"));
  public final CommandExceptionFactory NO_TEXT_CHANNEL = () -> new CommandException(RawMessages.get("error.no-text-channel"));
  public final CommandExceptionFactory1<TicketCloseResult> TICKET_CLOSE = result -> new CommandException(RawMessages.get("error.ticket.close"), new IllegalStateException("TicketCloseResult: " + result));
  public final CommandExceptionFactory TICKET_BOT_ADD = () -> new CommandException(RawMessages.get("error.ticket.bot.add"));
  public final CommandExceptionFactory TICKET_BOT_REMOVE = () -> new CommandException(RawMessages.get("error.ticket.bot.remove"));
  public final CommandExceptionFactory TICKET_MEMBER_ALREADY_IN_TICKET = () -> new CommandException(RawMessages.get("error.ticket.member.already-in-ticket"));
  public final CommandExceptionFactory TICKET_MEMBER_NOT_IN_TICKET = () -> new CommandException(RawMessages.get("error.ticket.member.not-in-ticket"));
  public final CommandExceptionFactory1<Throwable> TICKET_ADD_MEMBER = cause -> new CommandException(RawMessages.get("error.ticket.add.member"), cause);
  public final CommandExceptionFactory1<Throwable> TICKET_REMOVE_MEMBER = cause -> new CommandException(RawMessages.get("error.ticket.remove.member"), cause);
  public final CommandExceptionFactory TICKET_MEMBER_ALREADY_REMOVED = () -> new CommandException(RawMessages.get("error.ticket.remove.member.already-removed"));
  public final CommandExceptionFactory MINECRAFT_USER_NOT_FOUND = () -> new CommandException(RawMessages.get("error.minecraft.not-found"));
  public final CommandExceptionFactory TICKET_WHITELIST = () -> new CommandException(RawMessages.get("error.ticket.whitelist"));
  public final CommandExceptionFactory TICKET_WLQUERY_NO_USER = () -> new CommandException(RawMessages.get("error.ticket.wlquery.no-user"));
  public final CommandExceptionFactory1<String> WHITELIST_QUERY_NO_ENTRIES = name -> new CommandException(RawMessages.get("error.whitelist.query.no-results", name));
  public final CommandExceptionFactory WHITELIST_ROLE_NOT_REGISTERED = () -> new CommandException(RawMessages.get("error.whitelist.role-not-registered"));
  public final CommandExceptionFactory ARG_MISSING_USER = () -> new CommandException(RawMessages.get("error.command.arg.missing.user"));
  // @formatter:on
}
