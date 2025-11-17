package dev.slne.surf.discord.feedback

import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.dsl.sendEmbed
import dev.slne.surf.discord.interaction.button.ButtonRegistry
import dev.slne.surf.discord.messages.translatable
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component

@DiscordCommand("setup-feedback", "Sende das Feedback-Embed")
@Component
class FeedbackSetupCommand(
    private val buttonRegistry: ButtonRegistry
) : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        event.channel.sendEmbed {
            title = translatable("feedback.embed.title")
            description = translatable("feedback.embed.description")
        }.addComponents(ActionRow.of(buttonRegistry.get("feedback:create").button)).queue()

        event.reply(translatable("feedback.setup")).setEphemeral(true).queue()
    }
}