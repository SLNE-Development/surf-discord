package dev.slne.surf.discord.ticket.database.ticket.data

import dev.slne.surf.discord.ticket.TicketData
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.springframework.stereotype.Repository

@Repository
class TicketDataRepository {
    suspend fun setData(
        ticketId: ULong,
        data: TicketData
    ) = suspendTransaction {
        TicketDataTable.deleteWhere {
            TicketDataTable.ticketId eq ticketId
        }

        data.forEach { (key, value) ->
            TicketDataTable.insert {
                it[TicketDataTable.ticketId] = ticketId
                it[TicketDataTable.dataKey] = key
                it[TicketDataTable.dataValue] = value
            }
        }
    }

    suspend fun getData(
        ticketId: ULong
    ): TicketData = suspendTransaction {
        TicketDataTable.selectAll().where(TicketDataTable.ticketId eq ticketId).toList()
            .associate { it[TicketDataTable.dataKey] to it[TicketDataTable.dataValue] }
    }
}