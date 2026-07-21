package dev.slne.surf.discord.config

import dev.slne.surf.database.DatabaseApi
import dev.slne.surf.database.libs.io.r2dbc.pool.ConnectionPool
import dev.slne.surf.database.libs.io.r2dbc.pool.ConnectionPoolConfiguration
import dev.slne.surf.database.libs.io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import dev.slne.surf.database.libs.io.r2dbc.postgresql.PostgresqlConnectionFactory
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.vendors.PostgreSQLDialect
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.discord.logger
import dev.slne.surf.discord.ticket.database.deadline.DeadlineNotifyTable
import dev.slne.surf.discord.ticket.database.deadline.ReplyDeadlineTable
import dev.slne.surf.discord.ticket.database.members.TicketMemberTable
import dev.slne.surf.discord.ticket.database.messages.TicketMessagesTable
import dev.slne.surf.discord.ticket.database.messages.attachments.TicketAttachmentsTable
import dev.slne.surf.discord.ticket.database.ticket.TicketTable
import dev.slne.surf.discord.ticket.database.ticket.data.TicketDataTable
import dev.slne.surf.discord.ticket.database.ticket.staff.TicketStaffTable
import dev.slne.surf.discord.ticket.database.whitelist.FreebuildWhitelistTable
import dev.slne.surf.discord.ticket.database.whitelist.SocialConnectionsTable
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.jetbrains.annotations.ApiStatus
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@ApiStatus.Internal
@Serializable
data class DatabaseConfig(
    val hostname: String,
    val port: Int,
    val database: String,
    val username: String,
    val password: String,
    val schema: String = "public"
)

@Service
class DatabaseConfiguration {
    @Bean(destroyMethod = "shutdown")
    fun setupDatabase(): DatabaseApi {
        val config = botConfig.database
        val connectionConfiguration = PostgresqlConnectionConfiguration.builder()
            .host(config.hostname)
            .port(config.port)
            .database(config.database)
            .schema(config.schema)
            .username(config.username)
            .password(config.password)
            .build()
        val connectionFactory = PostgresqlConnectionFactory(connectionConfiguration)
        val poolConfiguration = ConnectionPoolConfiguration.builder(connectionFactory)
            .acquireRetry(1)
            .initialSize(1)
            .minIdle(1)
            .maxSize(10)
            .name("surf-discord")
            .build()

        return DatabaseApi.create(
            connectionFactory = ConnectionPool(poolConfiguration),
            dialect = PostgreSQLDialect()
        ).also {
            runBlocking {
                suspendTransaction {
                    SchemaUtils.create(
                        TicketTable,
                        TicketMemberTable,
                        TicketDataTable,
                        TicketStaffTable,
                        TicketMessagesTable,
                        TicketAttachmentsTable,
                        SocialConnectionsTable,
                        FreebuildWhitelistTable,
                        ReplyDeadlineTable,
                        DeadlineNotifyTable
                    )
                }
                logger.info("Connected to database (PostgreSQL) ${botConfig.database.database} at ${botConfig.database.hostname}:${botConfig.database.port}")
            }
        }
    }
}
