package dev.slne.surf.discord.command.console.impl

import dev.slne.surf.discord.command.console.ConsoleCommand
import dev.slne.surf.discord.ticket.database.ticket.TicketCrudRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class TicketCommand(
    private val discordScope: CoroutineScope,
) : ConsoleCommand {
    override val name = "ticket"

    private inline fun <T : Any> measure(block: () -> T): Pair<T, Long> {
        var result: T

        val time = measureTimeMillis {
            result = block()
        }

        return result to time
    }

    override fun execute(args: List<String>) {
        val amount = args.getOrElse(0) { "1" }.toInt()
        val times = mutableListOf<Long>()
        val client = TicketCrudRepository.CLIENT

        discordScope.launch {
            val (allTickets, time) = measure {
                client.findAll()
            }

            for (i in 1..amount) {
                val randomTicket = allTickets.random()
                val ticketUid = randomTicket.ticketUid

                val time = measureTimeMillis {
                    client.findByTicketId(ticketUid.toString())
                }

                println("Run $i: ${time}ms for ticket $ticketUid")

                times.add(time)
            }

            val min = times.minOrNull() ?: 0L
            val max = times.maxOrNull() ?: 0L
            val avg = if (times.isNotEmpty()) times.sum() / times.size else 0L

            println("Total fetch took ${time}ms for fetching all tickets.")
            println("Ticket fetch times for $amount runs:")
            println("Min: ${min}ms")
            println("Max: ${max}ms")
            println("Avg: ${avg}ms")
        }
    }
}