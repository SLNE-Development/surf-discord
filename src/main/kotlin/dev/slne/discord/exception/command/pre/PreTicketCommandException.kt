package dev.slne.discord.exception.command.pre

import dev.slne.discord.exception.DiscordException
import java.io.Serial

class PreTicketCommandException : PreCommandCheckException {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(exception: DiscordException) : super(exception)

    companion object {
        @Serial
        private val serialVersionUID = -1353290082500326289L
    }
}
