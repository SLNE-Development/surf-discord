package dev.slne.discord.discord.interaction.command.commands.misc

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.interactions.commands.choice
import dev.minn.jda.ktx.interactions.components.getOption
import dev.minn.jda.ktx.messages.MessageEdit
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.DiscordCommand
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.EmbedColors
import dev.slne.discord.message.translatable
import dev.slne.discord.cooldown.CooldownManager
import dev.slne.discord.cooldown.CooldownDuration
import dev.slne.discord.cooldown.CooldownKey
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook

private const val QUESTION_IDENTIFIER = "question"
private const val USER_IDENTIFIER = "user"

@DiscordCommandMeta(
    name = "faq",
    description = "Frequently asked questions",
    permission = CommandPermission.FAQ,
    ephemeral = false
)
class FAQCommand : DiscordCommand() {
    private val questions = listOf(
        Question(
            "connect-twitch",
            CooldownDuration.CONNECT_TWITCH,
            translatable("command.faq.questions.connect-twitch-with-discord"),
            translatable("command.faq.questions.connect-twitch-with-discord.answer")
        ),
        Question(
            "banned",
            CooldownDuration.BANNED,
            translatable("command.faq.questions.banned"),
            translatable("command.faq.questions.banned.answer")
        ),
        Question(
            "next-event",
            CooldownDuration.NEXT_EVENT,
            translatable("command.faq.questions.event"),
            translatable("command.faq.questions.event.answer")
        ),
        Question(
            "how-to-open-ticket",
            CooldownDuration.HOW_TO_OPEN_TICKET,
            translatable("command.faq.questions.open-ticket"),
            translatable("command.faq.questions.open-ticket.answer")
        ),
        Question(
            "rulebook",
            CooldownDuration.RULEBOOK,
            translatable("command.faq.questions.rulebook"),
            translatable("command.faq.questions.rulebook.answer")
        ),
        Question(
            "server-modpack",
            CooldownDuration.SERVER_MODPACK,
            translatable("command.faq.questions.server-modpack"),
            translatable("command.faq.questions.server-modpack.answer")
        ),
        Question(
            "problem-ressourcepack",
            CooldownDuration.PROBLEM_RESSOURCEPACK,
            translatable("command.faq.questions.problem-ressourcepack"),
            translatable("command.faq.questions.problem-ressourcepack.answer")
        ),
        Question(
            "problem-connection",
            CooldownDuration.PROBLEM_CONNECTION,
            translatable("command.faq.questions.problem-connection"),
            translatable("command.faq.questions.problem-connection.answer")
        ),
        Question(
            "read-the-docs",
            CooldownDuration.READ_THE_DOCS,
            translatable("command.faq.questions.read-the-docs"),
            translatable("command.faq.questions.read-the-docs.answer")
        ),
        Question(
            "maintenance",
            CooldownDuration.MAINTENANCE,
            translatable("command.faq.questions.maintenance"),
            translatable("command.faq.questions.maintenance.answer")
        ),
        Question(
            "how-to-share-log",
            CooldownDuration.HOW_TO_SHARE_LOG,
            translatable("command.faq.questions.how-to-share-log"),
            translatable("command.faq.questions.how-to-share-log.answer")
        ),
        Question(
            "clan-info",
            CooldownDuration.CLAN_INFO,
            translatable("command.faq.questions.clan-info"),
            translatable("command.faq.questions.clan-info.answer")
        ),
        Question(
            "take-part-in-event",
            CooldownDuration.TAKE_PART_IN_EVENT,
            translatable("command.faq.questions.take-part-in-event"),
            translatable("command.faq.questions.take-part-in-event.answer")
        )
    ).associateBy { it.identifier }

    override val options = listOf(
        option<String>(
            QUESTION_IDENTIFIER,
            translatable("command.faq.arg.question")
        ) {
            for ((_, question) in questions) {
                choice(question.identifier, question.identifier)
            }
        },
        option<User>(USER_IDENTIFIER, translatable("command.faq.arg.user"), required = false)
    )

    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        val identifier = interaction.getOptionOrThrow<String>(QUESTION_IDENTIFIER)
        val user = interaction.getOption<User>(USER_IDENTIFIER)
        val question = questions[identifier] ?: return

        val cooldownManager = CooldownManager()
        val commandName = interaction.name
        val channelId = interaction.channel.id.toLong()

        val key = CooldownKey(channelId, commandName)

        if (cooldownManager.isOnCooldown(channelId, commandName)) {
            val remainingSeconds = cooldownManager.getRemainingMillis(key) / 1000
            val message = translatable("interaction.command.cooldown.active", remainingSeconds.toString())
            hook.editOriginal(message).await()
            return
        }

        hook.editOriginal(MessageEdit {
            if (user != null) {
                content = user.asMention
            }

            embed {
                title = question.question
                description = question.answer
                color = EmbedColors.FAQ
            }
        }).await()

        cooldownManager.setCooldown(channelId, question.cooldownDuration)
    }

    private data class Question(val identifier: String, val cooldownDuration: CooldownDuration, val question: String, val answer: String)
}