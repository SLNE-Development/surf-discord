package dev.slne.surf.discord.command.impl

import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.interaction.modal.ModalRegistry
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component

@Component
@DiscordCommand("announcement-create", "Erstelle eine Ankündigung")
class AnnouncementCreateCommand(
    private val modalRegistry: ModalRegistry
) : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        val modal = modalRegistry.get("announcement:create").create()
        event.replyModal(modal).queue()
    }
}