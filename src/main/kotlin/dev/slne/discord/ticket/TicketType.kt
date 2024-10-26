package dev.slne.discord.ticket

import dev.minn.jda.ktx.emoji.toUnicodeEmoji
import dev.slne.discord.guild.permission.TicketViewPermission
import dev.slne.discord.message.translatable
import net.dv8tion.jda.api.entities.emoji.Emoji

enum class TicketType(
    val displayName: String,
    val configName: String,
    val description: String,
    val emoji: Emoji,
    val viewPermission: TicketViewPermission,
    val shouldPrintWlQuery: Boolean = true,
    val enabled: Boolean = true,
) {
    WHITELIST(
        translatable("modal.whitelist.title"),
        "whitelist",
        translatable("modal.whitelist.description"),
        "📜".toUnicodeEmoji(),
        TicketViewPermission.VIEW_WHITELIST_TICKETS,
        shouldPrintWlQuery = false
    ),

    SURVIVAL_SUPPORT(
        translatable("modal.support.survival.titel"),
        "survival-support",
        translatable("modal.support.survival.description"),
        "🛠️".toUnicodeEmoji(),
        TicketViewPermission.VIEW_SURVIVAL_SUPPORT_TICKETS,
    ),

    EVENT_SUPPORT(
        translatable("modal.support.event.titel"),
        "event-support",
        translatable("modal.support.event.description"),
        "🎉".toUnicodeEmoji(),
        TicketViewPermission.VIEW_EVENT_SUPPORT_TICKETS
    ),

    BUGREPORT(
        translatable("modal.bug-report.title"),
        "bugreport",
        translatable("modal.bug-report.description"),
        "🐞".toUnicodeEmoji(),
        TicketViewPermission.VIEW_BUGREPORT_TICKETS
    ),

    REPORT(
        translatable("modal.report.title"),
        "report",
        translatable("modal.report.description"),
        "📢".toUnicodeEmoji(),
        TicketViewPermission.VIEW_REPORT_TICKETS
    ),

    UNBAN(
        translatable("modal.unban.title"),
        "unban",
        translatable("modal.unban.description"),
        "🚫".toUnicodeEmoji(),
        TicketViewPermission.VIEW_UNBAN_TICKETS
    ),

    DISCORD_SUPPORT(
        translatable("modal.support.discord.titel"),
        "discord-support",
        translatable("modal.support.discord.description"),
        "💬".toUnicodeEmoji(),
        TicketViewPermission.VIEW_DISCORD_SUPPORT_TICKETS,
        shouldPrintWlQuery = false
    );

    companion object {
        fun fromChannelName(channelName: String): TicketType {
            val ticketTypeString = channelName.split("-").first()

            return entries.find { it.configName.replace('-', '_') == ticketTypeString }
                ?: throw IllegalArgumentException("TicketType not found for channel name: $channelName")
        }
    }
}

fun getTicketTypeByDisplayName(name: String) = TicketType.entries.find { it.displayName == name }

fun getTicketTypeByConfigName(name: String) = TicketType.entries.find { it.configName == name }