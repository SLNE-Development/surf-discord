package dev.slne.discord.util

import dev.slne.discord.exception.command.CommandException

fun interface ExceptionFactory<T : Throwable> {
    fun create(): T

    @FunctionalInterface
    interface CommandExceptionFactory : ExceptionFactory<CommandException>

    fun interface ExceptionFactory1<A, T : Throwable> {
        fun create(a: A): T

        @FunctionalInterface
        interface CommandExceptionFactory1<A> : ExceptionFactory1<A, CommandException>
    }
}