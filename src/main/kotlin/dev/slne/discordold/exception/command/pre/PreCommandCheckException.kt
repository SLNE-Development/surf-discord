package dev.slne.discordold.exception.command.pre

import dev.slne.discordold.exception.DiscordException
import java.io.Serial

open class PreCommandCheckException : DiscordException {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(exception: DiscordException) : super(exception)

    companion object {
        @Serial
        private const val serialVersionUID = 129434452849941329L
    }
}
