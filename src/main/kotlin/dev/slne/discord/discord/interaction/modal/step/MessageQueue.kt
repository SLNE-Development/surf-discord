package dev.slne.discord.discord.interaction.modal.step

import org.jetbrains.annotations.Contract
import java.util.*
import kotlin.math.min

/**
 * Represents a queue for managing messages, ensuring they conform to Discord's message length
 * limits.
 */
class MessageQueue {
    private val messageLines = LinkedList<String>()

    /**
     * Adds a message to the queue, splitting it into lines if necessary.
     *
     * @param message The message to add.
     * @return This queue.
     */
    @Contract("_ -> this")
    @Synchronized
    fun addMessage(message: String): MessageQueue {
        messageLines.addAll(
            Arrays.asList(
                *message.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            )
        )

        return this
    }

    /**
     * Adds an empty line to the queue.
     *
     * @return This queue.
     */
    @Synchronized
    fun addEmptyLine(): MessageQueue {
        return addMessage(" ")
    }

    /**
     * Adds a formatted message to the queue.
     *
     * @param message The message format.
     * @param args    Arguments referenced by the format specifiers in the message.
     * @return This queue.
     */
    @Synchronized
    fun addMessage(message: String, vararg args: Any?): MessageQueue {
        return addMessage(String.format(message, *args))
    }

    /**
     * Builds the queued messages into a list, ensuring they conform to Discord's length limits.
     *
     * @return A list of messages ready to be sent.
     */
    @Synchronized
    fun buildMessages(): LinkedList<String> {
        val messages = LinkedList<String>()
        var builder = StringBuilder()

        for (line in messageLines) {
            var start = 0
            while (start < line.length) {
                val end = min(
                    (start + MAX_MESSAGE_LENGTH).toDouble(),
                    line.length.toDouble()
                ).toInt()
                val substring = line.substring(start, end)

                if (builder.length + substring.length + 1 > MAX_MESSAGE_LENGTH) {
                    messages.add(builder.toString())
                    builder = StringBuilder()
                }

                if (!builder.isEmpty()) {
                    builder.append("\n")
                }

                builder.append(substring)
                start = end
            }
        }

        if (!builder.isEmpty()) {
            messages.add(builder.toString())
        }

        return messages
    }

    companion object {
        const val MAX_MESSAGE_LENGTH: Int = 2000
    }
}
