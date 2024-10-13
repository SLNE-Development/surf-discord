package dev.slne.discord.exception.command

import dev.slne.discord.message.RawMessages.Companion.get
import dev.slne.discord.ticket.result.TicketCloseResult
import dev.slne.discord.util.ExceptionFactory.CommandExceptionFactory
import dev.slne.discord.util.ExceptionFactory.ExceptionFactory1.CommandExceptionFactory1

object CommandExceptions {

    val GENERIC = CommandExceptionFactory { CommandException(get("error.generic")) }

    val NO_GUILD = CommandExceptionFactory { CommandException(get("error.no-guild")) }

    val SERVER_NOT_REGISTERED =
        CommandExceptionFactory { CommandException(get("error.server-not-registered")) }

    val NO_THREAD_CHANNEL =
        CommandExceptionFactory { CommandException(get("error.no-thread-channel")) }

    val TICKET_CLOSE =
        CommandExceptionFactory1 { result: TicketCloseResult ->
            CommandException(
                get("error.ticket.close"),
                IllegalStateException("TicketCloseResult: $result")
            )
        }

    val TICKET_BOT_ADD = CommandExceptionFactory { CommandException(get("error.ticket.bot.add")) }

    val TICKET_BOT_REMOVE =
        CommandExceptionFactory { CommandException(get("error.ticket.bot.remove")) }

    val TICKET_MEMBER_ALREADY_IN_TICKET =
        CommandExceptionFactory { CommandException(get("error.ticket.member.already-in-ticket")) }

    val TICKET_MEMBER_NOT_IN_TICKET =
        CommandExceptionFactory { CommandException(get("error.ticket.member.not-in-ticket")) }

    val TICKET_ADD_MEMBER =
        CommandExceptionFactory1 { cause: Throwable? ->
            CommandException(
                get("error.ticket.add.member"),
                cause
            )
        }

    val TICKET_REMOVE_MEMBER =
        CommandExceptionFactory1 { cause: Throwable? ->
            CommandException(
                get("error.ticket.remove.member"),
                cause
            )
        }

    val MINECRAFT_USER_NOT_FOUND =
        CommandExceptionFactory { CommandException(get("error.minecraft.not-found")) }

    val TICKET_WHITELIST =
        CommandExceptionFactory { CommandException(get("error.ticket.whitelist")) }

    val TICKET_WLQUERY_NO_USER =
        CommandExceptionFactory { CommandException(get("error.ticket.wlquery.no-user")) }

    val WHITELIST_QUERY_NO_ENTRIES =
        CommandExceptionFactory1 { name: String? ->
            CommandException(
                get(
                    "error.whitelist.query.no-results",
                    name
                )
            )
        }

    val WHITELIST_ROLE_NOT_REGISTERED =
        CommandExceptionFactory { CommandException(get("error.whitelist.role-not-registered")) }

    val ARG_MISSING_USER =
        CommandExceptionFactory { CommandException(get("error.command.arg.missing.user")) }

    val ARG_MISSING_ROLE =
        CommandExceptionFactory { CommandException(get("error.command.arg.missing.role")) }

    val ARG_MISSING_NUMBER =
        CommandExceptionFactory { CommandException(get("error.command.arg.missing.number")) }

    val ARG_MISSING_BOOLEAN =
        CommandExceptionFactory { CommandException(get("error.command.arg.missing.boolean")) }

    val ARG_MISSING_STRING =
        CommandExceptionFactory { CommandException(get("error.command.arg.missing.string")) }

    val ARG_MISSING_ATTACHMENT =
        CommandExceptionFactory { CommandException(get("error.command.arg.missing.attachment")) }
}
