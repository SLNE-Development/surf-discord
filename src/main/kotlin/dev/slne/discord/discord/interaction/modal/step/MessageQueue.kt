package dev.slne.discord.discord.interaction.modal.step

import java.util.*
import kotlin.math.min

const val MAX_MESSAGE_LENGTH = 2000

class MessageQueue {

    private val messageLines = mutableListOf<String>()

    @Synchronized
    fun addMessage(message: String): MessageQueue {
        messageLines.addAll(
            listOf(
                *message.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            )
        )

        return this
    }

    @Synchronized
    fun addEmptyLine(): MessageQueue {
        return addMessage(" ")
    }

    @Synchronized
    fun addMessage(message: String, vararg args: Any?): MessageQueue {
        return addMessage(String.format(message, *args))
    }

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
