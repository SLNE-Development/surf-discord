package dev.slne.surf.discord.config

import dev.slne.surf.discord.logger
import dev.slne.surf.discord.ticket.database.members.TicketMemberTable
import dev.slne.surf.discord.ticket.database.messages.TicketMessagesTable
import dev.slne.surf.discord.ticket.database.messages.attachments.TicketAttachmentsTable
import dev.slne.surf.discord.ticket.database.ticket.TicketTable
import dev.slne.surf.discord.ticket.database.ticket.data.TicketDataTable
import dev.slne.surf.discord.ticket.database.ticket.staff.TicketStaffTable
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@ApiStatus.Internal
@Serializable
data class DatabaseConfig(
    val hostname: String,
    val port: Int,
    val database: String,
    val username: String,
    val password: String
)

@Service
class DatabaseConfiguration {
    @Bean
    fun setupDatabase(): R2dbcDatabase = R2dbcDatabase.connect(
        url = "r2dbc:mariadb://${botConfig.database.hostname}:${botConfig.database.port}/${botConfig.database.database}",
        user = botConfig.database.username,
        password = botConfig.database.password,
    ).also {
        runBlocking {
            suspendTransaction {
                SchemaUtils.create(
                    TicketTable,
                    TicketMemberTable,
                    TicketDataTable,
                    TicketStaffTable,
                    TicketMessagesTable,
                    TicketAttachmentsTable
                )
            }
            logger.info("Connected to database ${botConfig.database.database} at ${botConfig.database.hostname}:${botConfig.database.port}")
        }
    }
}