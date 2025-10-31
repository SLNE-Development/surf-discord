package dev.slne.surf.discord.announcement.command

import dev.slne.surf.discord.announcement.AnnouncementService
import dev.slne.surf.discord.command.CommandOption
import dev.slne.surf.discord.command.CommandOptionType
import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.messages.translatable
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component

@Component
@DiscordCommand(
    "announcement-delete", "Lösche eine Ankündigung", options = [CommandOption(
        "announcement_id", "Die ID der Ankündigung, die gelöscht werden soll",
        CommandOptionType.STRING, true
    )]
)
class AnnouncementDeleteCommand(private val announcementService: AnnouncementService) :
    SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        val announcementMessageId = event.getOption("announcement_id")?.asString?.toLong() ?: return

        val announcement = announcementService.getAnnouncement(announcementMessageId)

        if (announcement == null) {
            event.reply(
                translatable(
                    "announcement.not-found",
                    announcementMessageId.toString()
                )
            )
                .setEphemeral(true).queue()
            return
        }

        announcementService.deleteAnnouncement(announcement)
        event.reply(translatable("announcement.deleted", announcementMessageId.toString()))
            .setEphemeral(true).queue()
    }
}