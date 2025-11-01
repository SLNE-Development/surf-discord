package dev.slne.surf.discord.contextmenu

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent

interface UserContextCommand {
    suspend fun execute(event: UserContextInteractionEvent)
}