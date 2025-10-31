package dev.slne.surf.discord.faq.command

import com.github.benmanes.caffeine.cache.Caffeine
import dev.slne.surf.discord.command.*
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.faq.Faq
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.util.Colors
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Component
@DiscordCommand(
    "faq", "Häufig gestellte Fragen anzeigen", options = [
        CommandOption(
            "question",
            "Die Frage, die angezeigt werden soll",
            CommandOptionType.STRING,
            true,
            choices = [
                CommandChoice("connect-twitch", "connect-twitch"),
                CommandChoice("banned", "banned"),
                CommandChoice("next-event", "next-event"),
                CommandChoice("how-to-open-ticket", "how-to-open-ticket"),
                CommandChoice("rulebook", "rulebook"),
                CommandChoice("server-modpack", "server-modpack"),
                CommandChoice("problem-resourcepack", "problem-resourcepack"),
                CommandChoice("problem-connection", "problem-connection"),
                CommandChoice("read-the-docs", "read-the-docs"),
                CommandChoice("maintenance", "maintenance"),
                CommandChoice("how-to-share-log", "how-to-share-log"),
                CommandChoice("clan-info", "clan-info"),
                CommandChoice("take-part-in-event", "take-part-in-event"),
                CommandChoice("survival-downtime", "survival-downtime"),
                CommandChoice("one-block-event", "one-block-event")
            ]
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
    private val faqCache = Caffeine.newBuilder()
        .expireAfterWrite(30.seconds.toJavaDuration())
        .build<Long, Pair<Faq, Long>>()

    override suspend fun execute(event: SlashCommandInteractionEvent) {
        val interaction = event.interaction

        val question = interaction.getOption("question")?.asString ?: return
        val user = interaction.getOption("user")?.asUser

        val faq = Faq.entries.find { it.id == question }

        if (faq == null) {
            event.reply(translatable("faq.not-found", question))
                .setEphemeral(true).queue()
            return
        }

        if (faqCache.asMap()
                .any { it.value.first == faq && it.value.second == event.messageChannel.idLong }
        ) {
            event.reply(translatable("faq.timeout")).setEphemeral(true).queue()
            return
        }

        faqCache.put(System.currentTimeMillis(), faq to event.messageChannel.idLong)

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
            color = Colors.INFO
        }).queue()
    }
}