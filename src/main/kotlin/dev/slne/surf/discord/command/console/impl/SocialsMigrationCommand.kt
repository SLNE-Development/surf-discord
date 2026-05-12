package dev.slne.surf.discord.command.console.impl

import dev.slne.surf.discord.command.console.ConsoleCommand
import dev.slne.surf.discord.ticket.database.whitelist.SocialsMigration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component

@Component
class SocialsMigrationCommand(
    private val discordScope: CoroutineScope
) : ConsoleCommand {
    override val name = "migrate-socials"

    override fun execute(args: List<String>) {
        discordScope.launch {
            println("Starting social whitelist migration...")
            SocialsMigration.migrateLegacy()

            println("\nMigration finished.")
        }
    }
}