package dev.slne.surf.discord.config

import dev.slne.surf.discord.logger
import dev.slne.surf.discord.ticket.database.deadline.DeadlineNotifyTable
import dev.slne.surf.discord.ticket.database.deadline.ReplyDeadlineTable
import dev.slne.surf.discord.ticket.database.members.TicketMemberTable
import dev.slne.surf.discord.ticket.database.messages.TicketMessagesTable
import dev.slne.surf.discord.ticket.database.messages.attachments.TicketAttachmentsTable
import dev.slne.surf.discord.ticket.database.migration.migratePostgreSqlUpsertConstraints
import dev.slne.surf.discord.ticket.database.ticket.TicketTable
import dev.slne.surf.discord.ticket.database.ticket.data.TicketDataTable
import dev.slne.surf.discord.ticket.database.ticket.staff.TicketStaffTable
import dev.slne.surf.discord.ticket.database.util.DiscordSchema
import org.jetbrains.exposed.v1.core.Table
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

internal val discordOwnedTables = arrayOf<Table>(
    TicketTable,
    TicketMemberTable,
    TicketDataTable,
    TicketStaffTable,
    TicketMessagesTable,
    TicketAttachmentsTable,
    ReplyDeadlineTable,
    DeadlineNotifyTable
)

@Service
class DatabaseConfiguration {
    @Bean
    fun setupDatabase(): R2dbcDatabase = R2dbcDatabase.connect(
        url = "r2dbc:postgresql://${botConfig.database.hostname}:${botConfig.database.port}/${botConfig.database.database}",
        user = botConfig.database.username,
        password = botConfig.database.password
    ).also {
        runBlocking {
            suspendTransaction {
                SchemaUtils.createSchema(DiscordSchema)
                SchemaUtils.create(*discordOwnedTables)
                migratePostgreSqlUpsertConstraints()
            }
            logger.info("Connected to database (PostgreSQL) ${botConfig.database.database} at ${botConfig.database.hostname}:${botConfig.database.port}")
        }
    }
}
