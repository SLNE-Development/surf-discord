package dev.slne.discord.exception.command

import dev.slne.discord.exception.DiscordException
import org.intellij.lang.annotations.Language
import java.io.Serial

class CommandException : DiscordException {
    constructor(@Language("markdown") message: String?) : super(message)

    constructor(@Language("markdown") message: String?, cause: Throwable?) : super(message, cause)

    constructor(exception: DiscordException) : super(exception)

    companion object {
        @Serial
        private val serialVersionUID = -2363935215977387153L
    }
}
