package dev.slne.discord.discord.interaction.modal.step;

import java.util.Arrays;
import java.util.LinkedList;

public final class MessageQueue {

  public static final int MAX_MESSAGE_LENGTH = 2000;
  private final LinkedList<String> messageLines = new LinkedList<>();

  public synchronized void addMessage(String message) {
    messageLines.addAll(Arrays.asList(message.split("\n")));
  }

  public synchronized void addMessage(String message, Object... args) {
    addMessage(String.format(message, args));
  }

  public synchronized LinkedList<String> buildMessages() {
    final LinkedList<String> messages = new LinkedList<>();
    StringBuilder builder = new StringBuilder();

    for (final String line : messageLines) {
      if (builder.length() + line.length() + 1 > MAX_MESSAGE_LENGTH) {
        messages.add(builder.toString());
        builder = new StringBuilder();
      }

      if (!builder.isEmpty()) {
        builder.append("\n");
      }

      builder.append(line);
    }

    if (!builder.isEmpty()) {
      messages.add(builder.toString());
    }

    return messages;
  }
}
