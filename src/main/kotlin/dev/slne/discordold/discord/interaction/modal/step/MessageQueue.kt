package dev.slne.discordold.discord.interaction.modal.step

import java.util.*
import kotlin.math.min

const val MAX_MESSAGE_LENGTH = 2000

class MessageQueue {

    private val messageLines = mutableListOf<String>()

    @Synchronized
    fun addMessage(message: String) = apply {
        messageLines.addAll(message.lineSequence().filter { it.isNotEmpty() }.toList())
    }

    @Synchronized
    fun addEmptyLine() = apply { addMessage(" ") }

    @Synchronized
    fun addMessage(message: String, vararg args: Any?) =
        apply { addMessage(String.format(message, *args)) }

    @Synchronized
    fun buildMessages(): LinkedList<String> {
        val messages = LinkedList<String>()
        var builder = StringBuilder()

        for (line in messageLines) {
            var start = 0

            while (start < line.length) {
                val end = min(
                    (start + MAX_MESSAGE_LENGTH),
                    line.length
                )

                val substring = line.substring(start, end)

                if (builder.length + substring.length + 1 > MAX_MESSAGE_LENGTH) {
                    messages.add(builder.toString())
                    builder = StringBuilder()
                }

                if (builder.isNotEmpty()) {
                    builder.append("\n")
                }

                builder.append(substring)
                start = end
            }
        }

        if (builder.isNotEmpty()) {
            messages.add(builder.toString())
        }

        return messages
    }
}
