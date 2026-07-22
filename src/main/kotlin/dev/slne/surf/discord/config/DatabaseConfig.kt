package dev.slne.surf.discord.config

import dev.slne.surf.database.DatabaseApi
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
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@Service
class DatabaseConfiguration {
    lateinit var databaseApi: DatabaseApi

    @Bean
    fun setupDatabase(): Boolean {
        val connectionConfig = PostgresqlConnectionConfiguration.builder()
            .host(EnvConfig.DB_HOST)
            .port(EnvConfig.DB_PORT)
            .database(EnvConfig.DB_NAME)
            .username(EnvConfig.DB_USERNAME)
            .password(EnvConfig.DB_PASSWORD)
            .apply { EnvConfig.DB_SCHEMA?.let { schema(it) } }
            .build()

        val connectionFactory = PostgresqlConnectionFactory(connectionConfig)

        databaseApi = DatabaseApi.create(connectionFactory, PostgreSQLDialect()).also {
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
                logger.info("Connected to database ${EnvConfig.DB_NAME} on ${EnvConfig.DB_HOST}:${EnvConfig.DB_PORT} as ${EnvConfig.DB_USERNAME}")
            }
        }

        return true
    }
}