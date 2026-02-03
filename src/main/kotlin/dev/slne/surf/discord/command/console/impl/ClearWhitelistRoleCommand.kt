package dev.slne.surf.discord.command.console.impl

import dev.minn.jda.ktx.coroutines.await
import dev.slne.surf.discord.command.console.ConsoleCommand
import dev.slne.surf.discord.config.botConfig
import dev.slne.surf.discord.jda
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.Member
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger

@Component
class ClearWhitelistRoleCommand(
    private val discordScope: CoroutineScope
) : ConsoleCommand {
    override val name = "clear-whitelist-role"

    override fun execute(args: List<String>) {
        println("Starting whitelist role cleanup…")

        discordScope.launch {
            val failed = mutableListOf<Member>()
            val targets = mutableListOf<Pair<Member, Long>>()

            jda.guilds.forEach { guild ->
                val role = guild.getRoleById(botConfig.whitelistedRoleId) ?: return@forEach
                val members = guild.findMembersWithRoles(role).await()
                members.forEach { member ->
                    targets += member to guild.idLong
                }
            }

            val total = targets.size
            if (total == 0) {
                println("No members with whitelist role found.")
                return@launch
            }

            val progress = AtomicInteger(0)

            fun renderBar(done: Int) {
                val width = 40
                val filled = (done * width) / total
                val bar = "█".repeat(filled) + "░".repeat(width - filled)
                val percent = (done * 100) / total
                print("\r[$bar] $percent% ($done/$total)")
            }

            renderBar(0)

            targets.forEach { (member, guildId) ->
                val guild = jda.getGuildById(guildId)
                val role = guild?.getRoleById(botConfig.whitelistedRoleId)

                runCatching {
                    if (guild != null && role != null) {
                        guild.removeRoleFromMember(member, role).await()
                    }
                }.onFailure {
                    failed += member
                }

                renderBar(progress.incrementAndGet())
            }

            println()
            println("Finished clearing whitelist roles.")

            if (failed.isNotEmpty()) {
                println("Failed members:")
                failed.distinctBy { it.id }.forEach {
                    println("- ${it.user.asTag} (${it.id})")
                }
            } else {
                println("All whitelist roles removed successfully.")
            }
        }
    }
}
