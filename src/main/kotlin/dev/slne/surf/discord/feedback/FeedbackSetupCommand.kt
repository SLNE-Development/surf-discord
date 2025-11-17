package dev.slne.surf.discord.feedback

import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.dsl.sendEmbed
import dev.slne.surf.discord.interaction.button.ButtonRegistry
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
            title = "Feedback einreichen"
            description =
                "Du hast Feedback oder Verbesserungsvorschläge für den Server? Dann kannst du diese hier einreichen. Unser Team wird dein Feedback prüfen und gegebenenfalls umsetzen."
        }.addComponents(ActionRow.of(buttonRegistry.get("feedback:create").button)).queue()
    }
}