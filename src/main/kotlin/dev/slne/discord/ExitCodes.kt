package dev.slne.discord

import kotlin.system.exitProcess

enum class ExitCodes(private val code: Int) {

    BOT_TOKEN_NOT_SET(1),
    FAILED_AWAIT_READY_JDA(2),
    CONFIG_FAILED_TO_LOAD(3);

    fun exit(): Nothing = exitProcess(code)
}
