package dev.slne.discord.exception

import java.io.Serial

abstract class DiscordException : Exception {
    
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(exception: DiscordException) : super(exception.message, exception)

    companion object {
        @Serial
        private const val serialVersionUID = 7077818795975848866L
    }
}
