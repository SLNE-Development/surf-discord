package dev.slne.surf.discord.config

import dev.slne.surf.discord.announcement.database.AnnouncementTable
import dev.slne.surf.discord.logger
import dev.slne.surf.discord.ticket.database.members.TicketMemberTable
import dev.slne.surf.discord.ticket.database.messages.TicketMessagesTable
import dev.slne.surf.discord.ticket.database.messages.attachments.TicketAttachmentsTable
import dev.slne.surf.discord.ticket.database.ticket.TicketTable
import dev.slne.surf.discord.ticket.database.ticket.data.TicketDataTable
import dev.slne.surf.discord.ticket.database.ticket.staff.TicketStaffTable
import kotlinx.serialization.Serializable
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
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
    fun setupDatabase(): Database = Database.connect(
        url = "jdbc:mariadb://${botConfig.database.hostname}:${botConfig.database.port}/${botConfig.database.database}",
        driver = "org.mariadb.jdbc.Driver",
        user = botConfig.database.username,
        password = botConfig.database.password,
    ).also {
        transaction {
            SchemaUtils.create(
                TicketTable,
                TicketMemberTable,
                TicketDataTable,
                TicketStaffTable,
                AnnouncementTable,
                TicketMessagesTable,
                TicketAttachmentsTable
            )// TODO: Remove for production
        }
        logger.info("Connected to database ${botConfig.database.database} at ${botConfig.database.hostname}:${botConfig.database.port}")
    }
}