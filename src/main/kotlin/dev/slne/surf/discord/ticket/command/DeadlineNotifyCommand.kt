package dev.slne.surf.discord.ticket.command

import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.interaction.modal.impl.DeadlineNotifyModal
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.ticket.database.deadline.DeadlineNotifyRepository
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component

@DiscordCommand(
    name = "deadline-notify",
    description = "Lege fest, ob du benachrichtigt wirst per  DM, wenn eine Reply-Deadline abläuft."
)
@Component
class DeadlineNotifyCommand(
    private val deadlineNotifyModal: DeadlineNotifyModal,
    private val deadlineNotifyRepository: DeadlineNotifyRepository,
) : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.TICKET_REPLY_DEADLINE)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val currentlyEnabled = deadlineNotifyRepository.isEnabled(event.user.idLong)

        event.replyModal(deadlineNotifyModal.build(currentlyEnabled)).queue()
    }
}
