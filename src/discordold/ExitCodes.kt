package dev.slne.discordold

import kotlin.system.exitProcess

enum class ExitCodes(private val code: Int) {

    FAILED_AWAIT_READY_JDA(1);

    fun exit(): Nothing = exitProcess(code)
}
