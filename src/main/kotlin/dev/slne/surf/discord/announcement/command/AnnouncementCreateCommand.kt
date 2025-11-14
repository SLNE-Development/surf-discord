package dev.slne.surf.discord.announcement.command

import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.interaction.modal.ModalRegistry
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component

@Component
@DiscordCommand(
    name = "announcement-create",
    description = "Erstelle eine Ankündigung"
)
class AnnouncementCreateCommand(
    private val modalRegistry: ModalRegistry
) : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.COMMAND_ANNOUNCEMENT_CREATE)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        event.replyModal(modalRegistry.get("announcement:create").create()).queue()
    }
}