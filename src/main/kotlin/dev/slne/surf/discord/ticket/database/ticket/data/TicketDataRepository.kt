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
    suspend fun setData(ticketUid: UUID, data: TicketData) =
        newSuspendedTransaction(Dispatchers.IO) {
            TicketDataTable.deleteWhere {
                TicketDataTable.ticketUid eq ticketUid
            }

            data.forEach { dta ->
                TicketDataTable.insert {
                    it[TicketDataTable.ticketUid] = ticketUid
                    it[TicketDataTable.dataKey] = dta.first
                    it[TicketDataTable.dataValue] = dta.second
                }
            }
        }

    suspend fun getData(ticketUid: UUID): TicketData =
        newSuspendedTransaction(Dispatchers.IO) {
            ObjectArraySet(
                TicketDataTable.selectAll().where(TicketDataTable.ticketUid eq ticketUid)
                    .map { it[TicketDataTable.dataKey] to it[TicketDataTable.dataValue] })
        }
}