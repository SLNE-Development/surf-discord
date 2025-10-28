package dev.slne.discordold.exception.ticket

import dev.slne.discordold.exception.DiscordException
import java.io.Serial

class UnableToGetTicketNameException : DiscordException {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(exception: DiscordException) : super(exception)

    companion object {
        @Serial
        private val serialVersionUID = -575479591760370835L
    }
}
