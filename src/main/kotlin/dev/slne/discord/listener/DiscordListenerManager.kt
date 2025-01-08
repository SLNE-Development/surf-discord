package dev.slne.discord.listener

import dev.slne.discord.discord.interaction.button.DiscordButtonProcessor
import dev.slne.discord.discord.interaction.command.DiscordCommandProcessor
import dev.slne.discord.discord.interaction.modal.step.ModalSelectionStep
import dev.slne.discord.listener.interaction.button.DiscordButtonListener
import dev.slne.discord.listener.interaction.command.CommandReceivedListener
import dev.slne.discord.listener.interaction.menu.DiscordSelectMenuListener
import dev.slne.discord.listener.interaction.modal.DiscordModalListener
import dev.slne.discord.listener.message.MessageCreatedListener
import dev.slne.discord.listener.message.MessageDeletedListener
import dev.slne.discord.listener.message.MessageUpdatedListener
import dev.slne.discord.listener.whitelist.WhitelistJoinListener
import dev.slne.discord.listener.whitelist.WhitelistQuitListener
import dev.slne.discord.persistence.service.ticket.TicketService
import dev.slne.discord.persistence.service.whitelist.WhitelistService
import net.dv8tion.jda.api.JDA
import org.springframework.stereotype.Component

@Component
class DiscordListenerManager(
    jda: JDA,
    buttonProcessor: DiscordButtonProcessor,
    commandProcessor: DiscordCommandProcessor,
    ticketService: TicketService,
    whitelistService: WhitelistService
) {

    init {
        DiscordModalListener(jda)
        ModalSelectionStep.ModalSelectionStepListener

        DiscordButtonListener(jda, buttonProcessor)
        CommandReceivedListener(jda, commandProcessor)
        DiscordSelectMenuListener(jda)

        MessageCreatedListener(jda, ticketService)
        MessageDeletedListener(jda, ticketService)
        MessageUpdatedListener(jda, ticketService)

        WhitelistJoinListener(jda, whitelistService)
        WhitelistQuitListener(jda, whitelistService)

//        TicketArchiveListener
    }
}