package dev.slne.surf.discord.faq.command

import dev.slne.surf.discord.command.CommandOption
import dev.slne.surf.discord.command.CommandOptionType
import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.faq.Faq
import dev.slne.surf.discord.util.Colors
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component

@Component
@DiscordCommand(
    "faq", "Häufig gestellte Fragen anzeigen", options = [
        CommandOption(
            "question",
            "Die Frage, die angezeigt werden soll",
            CommandOptionType.STRING,
            true,
            autocomplete = true
        ),
        CommandOption(
            "user",
            "Der Benutzer, für den die Frage angezeigt wird",
            CommandOptionType.USER,
            false
        )
    ]
)
class FaqCommand : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        val interaction = event.interaction

        val question = interaction.getOption("question")?.asString ?: return
        val user = interaction.getOption("user")?.asUser

        val faq = Faq.entries.find { it.id == question }

        if (faq == null) {
            event.reply("Es wurde keine FAQ mit der ID $question gefunden.")
                .setEphemeral(true).queue()
            return
        }

        if (user != null) {
            event.reply(user.asMention).setEmbeds(embed {
                title = faq.question
                description = faq.answer
                color = Colors.SUCCESS
            }).queue()
            return
        }

        event.replyEmbeds(embed {
            title = faq.question
            description = faq.answer
            color = Colors.SUCCESS
        }).queue()
    }
}