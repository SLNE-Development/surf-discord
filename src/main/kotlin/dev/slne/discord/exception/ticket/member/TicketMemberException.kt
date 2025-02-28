package dev.slne.discord.exception.ticket.member

import dev.slne.discord.exception.DiscordException
import java.io.Serial

abstract class TicketMemberException : DiscordException {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(exception: DiscordException) : super(exception)

    companion object {
        @Serial
        private const val serialVersionUID = 8277057907153655874L
    }
}
