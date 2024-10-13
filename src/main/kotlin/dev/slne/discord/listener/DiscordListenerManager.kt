package dev.slne.discord.listener

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

object DiscordListenerManager {

    init {
        DiscordModalListener
        ModalSelectionStep.ModalSelectionStepListener

        DiscordButtonListener
        CommandReceivedListener
        DiscordSelectMenuListener

        MessageCreatedListener
        MessageDeletedListener
        MessageUpdatedListener

        WhitelistJoinListener
        WhitelistQuitListener
    }
}