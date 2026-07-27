package dev.slne.surf.discord.config

import dev.slne.surf.database.DatabaseApi
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.Schema
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.Table
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.discord.DiscordBot
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
import kotlinx.serialization.Serializable
import org.jetbrains.annotations.ApiStatus

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

object DatabaseConfiguration {
    val databaseApi = DatabaseApi.create(DiscordBot.dataPath)

    suspend fun setupDatabase() {
        suspendTransaction {
            SchemaUtils.setSchema(Schema("surf-discord"))
            SchemaUtils.create(*discordOwnedTables)
            migratePostgreSqlUpsertConstraints()
        }
        logger.info("Connected to database (PostgreSQL)")
    }

    fun shutdown() {
        databaseApi.shutdown()
    }
}
