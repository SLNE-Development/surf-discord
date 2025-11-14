package dev.slne.surf.discord.announcement.command

import dev.slne.surf.discord.announcement.AnnouncementService
import dev.slne.surf.discord.command.CommandOption
import dev.slne.surf.discord.command.CommandOptionType
import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component

@Component
@DiscordCommand(
    name = "announcement-delete",
    description = "Lösche eine Ankündigung",
    options = [
        CommandOption(
            name = "announcement_id",
            description = "Die ID der Ankündigung, die gelöscht werden soll",
            type = CommandOptionType.STRING,
            required = true
        )
    ]
)
class AnnouncementDeleteCommand(
    private val announcementService: AnnouncementService
) : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.COMMAND_ANNOUNCEMENT_DELETE)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val announcementMessageId = event.getOption("announcement_id")?.asString?.toLong() ?: return
        val announcement = announcementService.getAnnouncement(announcementMessageId)

        if (announcement == null) {
            event.reply(
                translatable(
                    "announcement.not-found",
                    announcementMessageId.toString()
                )
            ).setEphemeral(true).queue()

            return
        }

        announcementService.deleteAnnouncement(announcement)
        
        event.reply(translatable("announcement.deleted", announcementMessageId.toString()))
            .setEphemeral(true).queue()
    }
}