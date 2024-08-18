package dev.slne.discord.discord.interaction.modal.step;

import java.util.Arrays;
import java.util.LinkedList;

public final class MessageQueue {
  private final LinkedList<String> messageLines = new LinkedList<>();

  public synchronized void addMessage(String message) {
    messageLines.addAll(Arrays.asList(message.split("\n")));
  }

  public synchronized void addMessage(String message, Object... args) {
    addMessage(String.format(message, args));
  }

  public synchronized String buildMessage() {
    final int size = messageLines.size();
    final StringBuilder builder = new StringBuilder();

    for (int i = 0; i < size; i++) {
      builder.append(messageLines.get(i));

      if (i < size - 1) {
        builder.append("\n");
      }
    }

    return builder.toString();
  }
}
