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

@Repository
class TicketDataRepository {
    suspend fun setData(ticketId: Long, data: TicketData) =
        newSuspendedTransaction(Dispatchers.IO) {
            println("Setting data for ticket ID $ticketId")
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

    suspend fun getData(ticketId: Long): TicketData =
        newSuspendedTransaction(Dispatchers.IO) {
            println("Getting data for ticket ID $ticketId")
            ObjectArraySet(
                TicketDataTable.selectAll().where(TicketDataTable.ticketId eq ticketId)
                    .map { it[TicketDataTable.dataKey] to it[TicketDataTable.dataValue] })
        }
}