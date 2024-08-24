package dev.slne.discord.exception.ticket.member;

public class TicketRemoveMemberException extends TicketMemberException {

  public TicketRemoveMemberException(String message) {
    super(message);
  }

  public TicketRemoveMemberException(String message, Throwable cause) {
    super(message, cause);
  }
}
