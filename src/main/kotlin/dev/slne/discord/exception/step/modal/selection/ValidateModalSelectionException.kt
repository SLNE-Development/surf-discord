package dev.slne.discord.exception.step.modal.selection

import dev.slne.discord.exception.DiscordException
import java.io.Serial

class ValidateModalSelectionException : DiscordException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)


    companion object {
        @JvmStatic
        @Serial
        private val serialVersionUID: Long = -7003651653574662944L
    }
}