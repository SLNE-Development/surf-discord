package dev.slne.discord.exception.ticket.member;

import java.io.Serial;

public class TicketAddMemberException extends TicketMemberException {

  @Serial
  private static final long serialVersionUID = -1462421070728035157L;

  public TicketAddMemberException(String message) {
    super(message);
  }

  public TicketAddMemberException(String message, Throwable cause) {
    super(message, cause);
  }
}
