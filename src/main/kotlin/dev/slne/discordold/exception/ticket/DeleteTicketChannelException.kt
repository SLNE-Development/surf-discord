package dev.slne.discordold.exception.ticket

import dev.slne.discordold.exception.DiscordException
import java.io.Serial

class DeleteTicketChannelException : DiscordException {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(exception: DiscordException) : super(exception)

    companion object {
        @Serial
        private val serialVersionUID = -2980471014795318817L
    }
}
