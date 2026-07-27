package dev.slne.surf.discord

import dev.slne.surf.api.core.util.runAtFixedRate
import dev.slne.surf.api.standalone.SurfApiStandaloneBootstrap
import dev.slne.surf.discord.DiscordBootstrap.enable
import dev.slne.surf.discord.command.CommandRegistrar
import dev.slne.surf.discord.command.console.ConsoleRunner
import dev.slne.surf.discord.config.DatabaseConfiguration
import dev.slne.surf.discord.contextmenu.ContextCommandRegistrar
import dev.slne.surf.discord.interaction.button.ButtonListener
import dev.slne.surf.discord.interaction.modal.ModalListener
import dev.slne.surf.discord.interaction.selectmenu.SelectMenuListener
import dev.slne.surf.discord.messages.MessageService
import dev.slne.surf.discord.premium.PremiumService
import dev.slne.surf.discord.redis.RedisService
import dev.slne.surf.discord.ticket.database.messages.TicketMessageListener
import dev.slne.surf.discord.ticket.deadline.ReplyDeadlineService
import dev.slne.surf.discord.ticket.listener.TicketArchivingListener
import dev.slne.surf.discord.ticket.listener.TicketLeaveListener
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.minutes

object DiscordBootstrap {
    suspend fun enable() {
        logger.info("Loading Discord Bot...")
        DiscordBot.createJda()

        DatabaseConfiguration.setupDatabase()
        RedisService.connect()

        ConsoleRunner.init()
        CommandRegistrar.init()
        ContextCommandRegistrar.registerAll()
        MessageService.loadMessages()

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
    }

    suspend fun shutdown() {
        logger.info("Stopping Discord Bot...")
        RedisService.disconnect()
        DatabaseConfiguration.shutdown()
    }
}

suspend fun main() {
    Runtime.getRuntime().addShutdownHook(Thread {
        runBlocking {
            DiscordBootstrap.shutdown()
            SurfApiStandaloneBootstrap.shutdown()
        }
    })

    enable()
}