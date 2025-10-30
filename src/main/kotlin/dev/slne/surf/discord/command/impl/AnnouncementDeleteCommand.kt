package dev.slne.surf.discord.command.impl

import dev.slne.surf.discord.announcement.AnnouncementService
import dev.slne.surf.discord.command.CommandOption
import dev.slne.surf.discord.command.CommandOptionType
import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
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
            event.reply("Es wurde keine Ankündigung mit der ID $announcementMessageId gefunden.")
                .setEphemeral(true).queue()
            return
        }

        announcementService.deleteAnnouncement(announcement)
        event.reply("Die Ankündigung mit der ID $announcementMessageId wurde gelöscht.")
            .setEphemeral(true).queue()
    }
}