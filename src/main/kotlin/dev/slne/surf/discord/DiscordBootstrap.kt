package dev.slne.surf.discord

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.util.runAtFixedRate
import dev.slne.surf.database.DatabaseApi
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.Schema
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.Table
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.discord.command.CommandRegistrar
import dev.slne.surf.discord.command.console.impl.*
import dev.slne.surf.discord.contextmenu.ContextCommandRegistrar
import dev.slne.surf.discord.interaction.button.ButtonListener
import dev.slne.surf.discord.interaction.modal.ModalListener
import dev.slne.surf.discord.interaction.selectmenu.SelectMenuListener
import dev.slne.surf.discord.messages.MessageService
import dev.slne.surf.discord.premium.PremiumService
import dev.slne.surf.discord.redis.RedisService
import dev.slne.surf.discord.ticket.database.deadline.DeadlineNotifyTable
import dev.slne.surf.discord.ticket.database.deadline.ReplyDeadlineTable
import dev.slne.surf.discord.ticket.database.members.TicketMemberTable
import dev.slne.surf.discord.ticket.database.messages.TicketMessageListener
import dev.slne.surf.discord.ticket.database.messages.TicketMessagesTable
import dev.slne.surf.discord.ticket.database.messages.attachments.TicketAttachmentsTable
import dev.slne.surf.discord.ticket.database.ticket.TicketTable
import dev.slne.surf.discord.ticket.database.ticket.data.TicketDataTable
import dev.slne.surf.discord.ticket.database.ticket.staff.TicketStaffTable
import dev.slne.surf.discord.ticket.deadline.ReplyDeadlineService
import dev.slne.surf.discord.ticket.listener.TicketArchivingListener
import dev.slne.surf.discord.ticket.listener.TicketLeaveListener
import dev.slne.surf.discord.util.Emojis
import dev.slne.surf.microservice.api.microservice.Microservice
import kotlin.io.path.Path
import kotlin.time.Duration.Companion.minutes

private val discordOwnedTables = arrayOf<Table>(
    TicketTable,
    TicketMemberTable,
    TicketDataTable,
    TicketStaffTable,
    TicketMessagesTable,
    TicketAttachmentsTable,
    ReplyDeadlineTable,
    DeadlineNotifyTable
)

@AutoService(Microservice::class)
class DiscordBootstrap : Microservice() {
    override val dataPath = Path("config")
    private val databaseApi = DatabaseApi.create(dataPath)

    override suspend fun onBootstrap(args: List<String>) {
        logger.info("Loading Discord Bot...")
        DiscordBot.createJda()

        suspendTransaction {
            SchemaUtils.setSchema(Schema("surf-discord"))
            SchemaUtils.create(*discordOwnedTables)
        }

        logger.info("Connected to database (PostgreSQL)")

        RedisService.connect()
        CommandRegistrar.init()
        ContextCommandRegistrar.registerAll()
        MessageService.loadMessages()
        Emojis.updateEmojis()

        EmojiCreateCommand.register()
        HelpCommand.register()
        InfoCommand.register()
        RegisterCommand.register()
        UnregisterCommandsCommand.register()

        jda.addEventListener(
            TicketArchivingListener,
            TicketMessageListener,
            TicketLeaveListener,
            SelectMenuListener,
            ButtonListener,
            ModalListener,
            ContextCommandRegistrar
        )

        discordScope.runAtFixedRate(1.minutes) {
            ReplyDeadlineService.checkExpiredDeadlines()
        }

        discordScope.runAtFixedRate(2.minutes) {
            PremiumService.syncPremium()
        }

        logger.info("Done!")
    }

    override suspend fun onDisable() {
        logger.info("Stopping Discord Bot...")
        RedisService.disconnect()

        databaseApi.shutdown()

        logger.info("Shutdown complete. Byeeee!")
    }
}