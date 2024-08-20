package dev.slne.discord.discord.interaction.modal.step;

import java.util.Arrays;
import java.util.LinkedList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a queue for managing messages, ensuring they conform to Discord's message length
 * limits.
 */
public final class MessageQueue {

  public static final int MAX_MESSAGE_LENGTH = 2000;
  private final LinkedList<String> messageLines = new LinkedList<>();

  /**
   * Adds a message to the queue, splitting it into lines if necessary.
   *
   * @param message The message to add.
   * @return This queue.
   */
  @Contract("_ -> this")
  public synchronized MessageQueue addMessage(@NotNull String message) {
    messageLines.addAll(Arrays.asList(message.split("\n")));

    return this;
  }

  /**
   * Adds an empty line to the queue.
   *
   * @return This queue.
   */
  public synchronized MessageQueue addEmptyLine() {
    return addMessage(" ");
  }

  /**
   * Adds a formatted message to the queue.
   *
   * @param message The message format.
   * @param args    Arguments referenced by the format specifiers in the message.
   * @return This queue.
   */
  public synchronized MessageQueue addMessage(String message, Object... args) {
    return addMessage(String.format(message, args));
  }

  /**
   * Builds the queued messages into a list, ensuring they conform to Discord's length limits.
   *
   * @return A list of messages ready to be sent.
   */
  public synchronized @NotNull LinkedList<String> buildMessages() {
    final LinkedList<String> messages = new LinkedList<>();
    StringBuilder builder = new StringBuilder();

    for (final String line : messageLines) {
      int start = 0;
      while (start < line.length()) {
        final int end = Math.min(start + MAX_MESSAGE_LENGTH, line.length());
        final String substring = line.substring(start, end);

        if (builder.length() + substring.length() + 1 > MAX_MESSAGE_LENGTH) {
          messages.add(builder.toString());
          builder = new StringBuilder();
        }

        if (!builder.isEmpty()) {
          builder.append("\n");
        }

        builder.append(substring);
        start = end;
      }
    }

    if (!builder.isEmpty()) {
      messages.add(builder.toString());
    }

    return messages;
  }
}
