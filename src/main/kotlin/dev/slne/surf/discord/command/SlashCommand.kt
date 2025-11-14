package dev.slne.surf.discord.command

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

interface SlashCommand {
    suspend fun execute(event: SlashCommandInteractionEvent)
}
