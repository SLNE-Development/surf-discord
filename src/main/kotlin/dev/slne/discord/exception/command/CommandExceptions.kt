package dev.slne.discord.exception.command

import dev.slne.discord.message.translatable
import dev.slne.discord.ticket.result.TicketCloseResult
import dev.slne.discord.util.ExceptionFactory.CommandExceptionFactory
import dev.slne.discord.util.ExceptionFactory.ExceptionFactory1.CommandExceptionFactory1

object CommandExceptions {

    val GENERIC = CommandExceptionFactory { CommandException(translatable("error.generic")) }

    val NO_GUILD = CommandExceptionFactory { CommandException(translatable("error.no-guild")) }

    val SERVER_NOT_REGISTERED =
        CommandExceptionFactory { CommandException(translatable("error.server-not-registered")) }

    val NO_THREAD_CHANNEL =
        CommandExceptionFactory { CommandException(translatable("error.no-thread-channel")) }

    val TICKET_CLOSE =
        CommandExceptionFactory1 { result: TicketCloseResult ->
            CommandException(
                translatable("error.ticket.close"),
                IllegalStateException("TicketCloseResult: $result")
            )
        }

    val TICKET_BOT_ADD =
        CommandExceptionFactory { CommandException(translatable("error.ticket.bot.add")) }

    val TICKET_BOT_REMOVE =
        CommandExceptionFactory { CommandException(translatable("error.ticket.bot.remove")) }

    val TICKET_MEMBER_ALREADY_IN_TICKET =
        CommandExceptionFactory { CommandException(translatable("error.ticket.member.already-in-ticket")) }

    val TICKET_MEMBER_NOT_IN_TICKET =
        CommandExceptionFactory { CommandException(translatable("error.ticket.member.not-in-ticket")) }

    val TICKET_ADD_MEMBER =
        CommandExceptionFactory1 { cause: Throwable? ->
            CommandException(
                translatable("error.ticket.add.member"),
                cause
            )
        }

    val TICKET_REMOVE_MEMBER =
        CommandExceptionFactory1 { cause: Throwable? ->
            CommandException(
                translatable("error.ticket.remove.member"),
                cause
            )
        }

    val MINECRAFT_USER_NOT_FOUND =
        CommandExceptionFactory { CommandException(translatable("error.minecraft.not-found")) }

    val TICKET_WHITELIST =
        CommandExceptionFactory { CommandException(translatable("error.ticket.whitelist")) }

    val TICKET_WLQUERY_NO_USER =
        CommandExceptionFactory { CommandException(translatable("error.ticket.wlquery.no-user")) }

    val WHITELIST_QUERY_NO_ENTRIES =
        CommandExceptionFactory1 { name: String? ->
            CommandException(
                translatable(
                    "error.whitelist.query.no-results",
                    name
                )
            )
        }

    val WHITELIST_ROLE_NOT_REGISTERED =
        CommandExceptionFactory { CommandException(translatable("error.whitelist.role-not-registered")) }

    val ARG_MISSING_USER =
        CommandExceptionFactory { CommandException(translatable("error.command.arg.missing.user")) }

    val ARG_MISSING_ROLE =
        CommandExceptionFactory { CommandException(translatable("error.command.arg.missing.role")) }

    val ARG_MISSING_NUMBER =
        CommandExceptionFactory { CommandException(translatable("error.command.arg.missing.number")) }

    val ARG_MISSING_BOOLEAN =
        CommandExceptionFactory { CommandException(translatable("error.command.arg.missing.boolean")) }

    val ARG_MISSING_STRING =
        CommandExceptionFactory { CommandException(translatable("error.command.arg.missing.string")) }

    val ARG_MISSING_ATTACHMENT =
        CommandExceptionFactory { CommandException(translatable("error.command.arg.missing.attachment")) }
}
