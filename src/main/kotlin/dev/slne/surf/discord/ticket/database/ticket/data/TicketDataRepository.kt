package dev.slne.surf.discord.ticket.database.ticket.data

import dev.slne.surf.discord.ticket.TicketData
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class TicketDataRepository {
    suspend fun setData(
        ticketId: UUID,
        data: TicketData
    ) = newSuspendedTransaction(Dispatchers.IO) {
        TicketDataTable.deleteWhere {
            TicketDataTable.ticketUid eq ticketId
        }

        data.forEach { (key, value) ->
            TicketDataTable.insert {
                it[TicketDataTable.ticketUid] = ticketId
                it[TicketDataTable.dataKey] = key
                it[TicketDataTable.dataValue] = value
            }
        }
    }

    suspend fun getData(
        ticketId: UUID
    ): TicketData = newSuspendedTransaction(Dispatchers.IO) {
        TicketDataTable.selectAll().where(TicketDataTable.ticketUid eq ticketId)
            .associate { it[TicketDataTable.dataKey] to it[TicketDataTable.dataValue] }
    }
}