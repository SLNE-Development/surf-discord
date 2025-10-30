package dev.slne.surf.discord.command.impl

import dev.slne.surf.discord.announcement.AnnouncementService
import dev.slne.surf.discord.command.CommandOption
import dev.slne.surf.discord.command.CommandOptionType
import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.interaction.modal.ModalRegistry
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component

@DiscordCommand(
    "announcement-edit", "Bearbeite eine Ankündigung", options = [CommandOption(
        "message_id", "Die ID der Ankündigungsnachricht",
        CommandOptionType.STRING, true
    )]
)
@Component
class AnnouncementEditCommand(
    private val announcementService: AnnouncementService
) : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        val messageId = event.getOption("message_id")?.asLong ?: return

        if (!announcementService.isAnnouncement(messageId)) {
            event.reply("Es wurde keine Ankündigung mit der angegebenen Nachricht-ID gefunden.")
                .setEphemeral(true).queue()
            return
        }

        val announcement =
            announcementService.getAnnouncement(messageId) ?: error("Announcement should exist")

        val modal = getBean<ModalRegistry>().get("announcement:edit").create(
            event.hook,
            announcement.messageId.toString(),
            announcement.title,
            announcement.content
        )

        event.replyModal(modal).queue()
    }
}