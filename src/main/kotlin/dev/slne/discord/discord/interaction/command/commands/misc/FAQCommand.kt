package dev.slne.discord.discord.interaction.command.commands.misc

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.interactions.commands.choice
import dev.minn.jda.ktx.interactions.components.getOption
import dev.minn.jda.ktx.messages.MessageEdit
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.config.botConfig
import dev.slne.discord.discord.interaction.command.DiscordCommand
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.EmbedColors
import dev.slne.discord.message.translatable
import dev.slne.discord.util.CooldownLock
import net.dv8tion.jda.api.entities.Guild
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
    private val cooldownLock = CooldownLock(botConfig.cooldown.faqCooldown)

    private val questions = listOf(
        Question(
            "connect-twitch",
            translatable("command.faq.questions.connect-twitch-with-discord"),
            translatable("command.faq.questions.connect-twitch-with-discord.answer")
        ),
        Question(
            "banned",
            translatable("command.faq.questions.banned"),
            translatable("command.faq.questions.banned.answer")
        ),
        Question(
            "next-event",
            translatable("command.faq.questions.event"),
            translatable("command.faq.questions.event.answer")
        ),
        Question(
            "how-to-open-ticket",
            translatable("command.faq.questions.open-ticket"),
            translatable("command.faq.questions.open-ticket.answer")
        ),
        Question(
            "rulebook",
            translatable("command.faq.questions.rulebook"),
            translatable("command.faq.questions.rulebook.answer")
        ),
        Question(
            "server-modpack",
            translatable("command.faq.questions.server-modpack"),
            translatable("command.faq.questions.server-modpack.answer")
        ),
        Question(
            "problem-ressourcepack",
            translatable("command.faq.questions.problem-ressourcepack"),
            translatable("command.faq.questions.problem-ressourcepack.answer")
        ),
        Question(
            "problem-connection",
            translatable("command.faq.questions.problem-connection"),
            translatable("command.faq.questions.problem-connection.answer")
        ),
        Question(
            "read-the-docs",
            translatable("command.faq.questions.read-the-docs"),
            translatable("command.faq.questions.read-the-docs.answer")
        ),
        Question(
            "maintenance",
            translatable("command.faq.questions.maintenance"),
            translatable("command.faq.questions.maintenance.answer")
        ),
        Question(
            "how-to-share-log",
            translatable("command.faq.questions.how-to-share-log"),
            translatable("command.faq.questions.how-to-share-log.answer")
        ),
        Question(
            "clan-info",
            translatable("command.faq.questions.clan-info"),
            translatable("command.faq.questions.clan-info.answer")
        ),
        Question(
            "take-part-in-event",
            translatable("command.faq.questions.take-part-in-event"),
            translatable("command.faq.questions.take-part-in-event.answer")
        ),
        Question(
            "survival-downtime",
            translatable("command.faq.questions.survival-downtime"),
            translatable("command.faq.questions.survival-downtime.answer")
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

    override fun performFirstChecksWithNoPermissionValidationOnlyUseIfYouKnowWhatYouAreDoing(
        user: User,
        guild: Guild,
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ): Boolean {
        val qIdentifier =
            interaction.getOption<String>(QUESTION_IDENTIFIER) ?: return true // handled in internalExecute
        val identifier = "${interaction.channel.id}-$qIdentifier"

        if (!cooldownLock.acquire(qIdentifier)) {
            throw CommandExceptions.ON_COOLDOWN.create(cooldownLock.cooldown(identifier))
        }
        
        return true
    }

    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        val identifier = interaction.getOptionOrThrow<String>(QUESTION_IDENTIFIER)
        val user = interaction.getOption<User>(USER_IDENTIFIER)
        val question = questions[identifier] ?: return

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
    }

    private data class Question(val identifier: String, val question: String, val answer: String)
}