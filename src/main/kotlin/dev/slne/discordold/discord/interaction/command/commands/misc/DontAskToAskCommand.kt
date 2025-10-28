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
    name = "ask",
    description = "Fordert Nutzer auf, ihre Fragen direkt im Kanal zu stellen, ohne vorher um Erlaubnis zu fragen",
    permission = CommandPermission.DONT_ASK_TO_ASK,
    ephemeral = false
)
class DontAskToAskCommand : DiscordCommand() {
    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        hook.editOriginalEmbeds(Embed {
            title = translatable("command.dontasktoask.message.title")
            description = translatable("command.dontasktoask.message.content")
            color = EmbedColors.DO_NOT_ASK
        }).await()
    }
}