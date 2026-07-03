package dev.slne.surf.discord.faq.command

import com.github.benmanes.caffeine.cache.Caffeine
import dev.slne.surf.discord.command.*
import dev.slne.surf.discord.faq.Faq
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.util.Colors
import net.dv8tion.jda.api.components.container.Container
import net.dv8tion.jda.api.components.container.ContainerChildComponent
import net.dv8tion.jda.api.components.mediagallery.MediaGallery
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem
import net.dv8tion.jda.api.components.separator.Separator
import net.dv8tion.jda.api.components.textdisplay.TextDisplay
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.utils.FileUpload
import org.springframework.stereotype.Component
import java.io.File
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
                CommandChoice("problem-nrc-voice-chat", "problem-nrc-voice-chat"),
                CommandChoice("read-the-docs", "read-the-docs"),
                CommandChoice("maintenance", "maintenance"),
                CommandChoice("how-to-share-log", "how-to-share-log"),
                CommandChoice("clan-info", "clan-info"),
                CommandChoice("take-part-in-event", "take-part-in-event"),
                CommandChoice("how-to-join", "how-to-join"),
                CommandChoice("ask", "ask"),
                CommandChoice("missing-information", "missing-information"),
                CommandChoice("how-to-whitelist", "how-to-whitelist"),
                CommandChoice("ping-pong", "ping-pong")
            ]
        ),
        CommandOption(
            "user",
            "Der Benutzer, für den die Frage angezeigt wird",
            CommandOptionType.USER,
            false
        ),
        CommandOption(
            "info",
            "Zeigt das FAQ nur dir selbst an",
            CommandOptionType.BOOLEAN,
            false
        )
    ]
)
class FaqCommand : SlashCommand {
    private val faqCache = Caffeine.newBuilder()
        .expireAfterWrite(30.seconds.toJavaDuration())
        .build<Long, Pair<Faq, Long>>()

    private fun faqComponent(faq: Faq, userMention: String? = null): Container {
        val components = mutableListOf<ContainerChildComponent>()

        if (userMention != null) {
            components += TextDisplay.of(userMention)
            components += Separator.createDivider(Separator.Spacing.SMALL)
        }

        components += TextDisplay.of("## ${faq.question}")
        components += TextDisplay.of(faq.answer)

        faq.attachmentPath?.let(::File)?.let { file ->
            components += Separator.createDivider(Separator.Spacing.LARGE)
            components += MediaGallery.of(MediaGalleryItem.fromFile(FileUpload.fromData(file)))
        }

        return Container.of(components).withAccentColor(Colors.INFO)
    }

    override suspend fun execute(event: SlashCommandInteractionEvent) {
        val interaction = event.interaction
        val question = interaction.getOption("question")?.asString ?: return
        val user = interaction.getOption("user")?.asUser
        val info = interaction.getOption("info")?.asBoolean ?: false
        val faq = Faq.entries.find { it.id == question }

        if (!event.member.hasPermission(DiscordPermission.COMMAND_FAQ)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }


        if (faq == null) {
            event.reply(translatable("faq.not-found", question))
                .setEphemeral(true).queue()

            return
        }

        if (info) {
            event.replyComponents(faqComponent(faq))
                .useComponentsV2()
                .setEphemeral(true)
                .queue()

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
            event.replyComponents(faqComponent(faq, user.asMention))
                .useComponentsV2()
                .mention(user)
                .queue()

            return
        }

        event.replyComponents(faqComponent(faq))
            .useComponentsV2()
            .queue()
    }
}
