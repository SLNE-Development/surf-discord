package dev.slne.discord.discord.interaction.command.commands.misc

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.messages.Embed
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.DiscordCommand
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.EmbedColors
import dev.slne.discord.message.translatable
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook

@DiscordCommandMeta(
    name = "ask",
    description = "Fordert Nutzer auf, ihre Fragen direkt im Kanal zu stellen, ohne vorher um Erlaubnis zu fragen",
    permission = CommandPermission.DONT_ASK_TO_ASK,
    ephemeral = false
)
class DontAskToAsk : DiscordCommand() {
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