package dev.slne.surf.discord.faq.command

import com.github.benmanes.caffeine.cache.Caffeine
import dev.slne.surf.discord.command.*
import dev.slne.surf.discord.faq.FaqEntry
import dev.slne.surf.discord.faq.FaqPlatform
import dev.slne.surf.discord.faq.FaqSender
import dev.slne.surf.discord.faq.FaqService
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.util.Colors
import net.dv8tion.jda.api.components.container.Container
import net.dv8tion.jda.api.components.textdisplay.TextDisplay
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

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
        ),
        CommandOption(
            "info",
            "Zeigt das FAQ nur dir selbst an",
            CommandOptionType.BOOLEAN,
            false
        )
    ]
)
object FaqCommand : SlashCommand {
    private val faqCache = Caffeine.newBuilder()
        .expireAfterWrite(30.seconds.toJavaDuration())
        .build<Long, Pair<String, Long>>()

    private fun faqComponent(faq: FaqEntry, userMention: String? = null): Container {
        val translation = faq.translation()
        val components = mutableListOf(
            TextDisplay.of("## ${translation.question}"),
            TextDisplay.of(translation.longText)
        )

        if (userMention != null) {
            components += TextDisplay.of("-# $userMention")
        }

        return Container.of(components).withAccentColor(Colors.INFO)
    }

    override suspend fun autocomplete(event: CommandAutoCompleteInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.COMMAND_FAQ)) {
            event.replyChoices(emptyList()).queue()
            return
        }

        val choices = FaqService.search(event.focusedOption.value, FaqPlatform.DISCORD)
            .map { Command.Choice(it.key, it.key) }

        event.replyChoices(choices).queue()
    }

    override suspend fun execute(event: SlashCommandInteractionEvent) {
        val interaction = event.interaction
        val question = interaction.getOption("question")?.asString ?: return
        val user = interaction.getOption("user")?.asUser
        val info = interaction.getOption("info")?.asBoolean ?: false

        if (!event.member.hasPermission(DiscordPermission.COMMAND_FAQ)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val faq = FaqService.getActive(question, FaqPlatform.DISCORD)

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
                .any { it.value.first == faq.key && it.value.second == event.messageChannel.idLong }
        ) {
            event.reply(translatable("faq.timeout")).setEphemeral(true).queue()
            return
        }

        faqCache.put(System.currentTimeMillis(), faq.key to event.messageChannel.idLong)

        if (user != null) {
            event.replyComponents(faqComponent(faq, user.asMention))
                .useComponentsV2()
                .mention(user)
                .queue()
        } else {
            event.replyComponents(faqComponent(faq))
                .useComponentsV2()
                .queue()
        }

        FaqService.recordUsage(faq, FaqPlatform.DISCORD, FaqSender(event.user.id, event.user.name))
    }
}
