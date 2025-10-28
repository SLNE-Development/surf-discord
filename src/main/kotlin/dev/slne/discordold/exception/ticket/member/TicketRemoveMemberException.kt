package dev.slne.discordold.exception.ticket.member

import dev.slne.discordold.exception.DiscordException
import java.io.Serial

class TicketRemoveMemberException : TicketMemberException {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(exception: DiscordException) : super(exception)

    companion object {
        @Serial
        private val serialVersionUID = -196685204706749789L
    }
}
