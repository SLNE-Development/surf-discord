package dev.slne.surf.discord.ticket.database.ticket.data

import dev.slne.surf.discord.ticket.TicketData
import it.unimi.dsi.fastutil.objects.ObjectArraySet
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
            TicketDataTable.ticketId eq ticketId
        }

        data.forEach { dta ->
            TicketDataTable.insert {
                it[TicketDataTable.ticketId] = ticketId
                it[TicketDataTable.dataKey] = dta.first
                it[TicketDataTable.dataValue] = dta.second
            }
        }
    }

    suspend fun getData(
        ticketId: UUID
    ): TicketData = newSuspendedTransaction(Dispatchers.IO) {
        TicketDataTable.selectAll().where(TicketDataTable.ticketId eq ticketId)
            .mapTo(ObjectArraySet()) { it[TicketDataTable.dataKey] to it[TicketDataTable.dataValue] }
    }
}