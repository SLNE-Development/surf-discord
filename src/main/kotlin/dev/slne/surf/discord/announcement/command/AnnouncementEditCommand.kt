package dev.slne.surf.discord.announcement.command

import dev.slne.surf.discord.announcement.AnnouncementService
import dev.slne.surf.discord.command.CommandOption
import dev.slne.surf.discord.command.CommandOptionType
import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.interaction.modal.ModalRegistry
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
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
        if (!event.member.hasPermission(DiscordPermission.COMMAND_ANNOUNCEMENT_EDIT)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }
        val messageId = event.getOption("message_id")?.asLong ?: return

        if (!announcementService.isAnnouncement(messageId)) {
            event.reply(translatable("announcement.not-found", messageId.toString()))
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