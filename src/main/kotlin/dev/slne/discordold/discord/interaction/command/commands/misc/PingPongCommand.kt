package dev.slne.discordold.discord.interaction.command.commands.misc

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.messages.Embed
import dev.slne.discordold.annotation.DiscordCommandMeta
import dev.slne.discordold.discord.interaction.command.DiscordCommand
import dev.slne.discordold.guild.permission.CommandPermission
import dev.slne.discordold.message.EmbedColors
import dev.slne.discordold.message.translatable
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook

@DiscordCommandMeta(
    name = "ping-pong",
    description = "Informiert die Nutzer über Antwortzeiten und Erwähnungen",
    permission = CommandPermission.PING_PONG,
    ephemeral = false
)
class PingPongCommand : DiscordCommand() {
    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        hook.editOriginalEmbeds(Embed {
            title = translatable("command.pingpong.message.title")
            description = translatable("command.pingpong.message.content")
            color = EmbedColors.PING_PONG
        }).await()
    }
}
