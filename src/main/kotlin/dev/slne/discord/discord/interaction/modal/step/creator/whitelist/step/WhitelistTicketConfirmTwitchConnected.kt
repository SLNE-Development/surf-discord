package dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step

import dev.minn.jda.ktx.emoji.toUnicodeEmoji
import dev.minn.jda.ktx.interactions.components.InlineModal
import dev.minn.jda.ktx.interactions.components.SelectOption
import dev.slne.discord.discord.interaction.modal.step.ModalSelectionStep
import dev.slne.discord.message.RawMessages.Companion.get
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

private const val TWITCH_CONNECT_TUTORIAL = "https://server.castcrafter.de/support.html#link-twitch"
private const val OPTION_YES = "yes"
private const val OPTION_NO = "no"

class WhitelistTicketConfirmTwitchConnected : ModalSelectionStep(
    get("modal.whitelist.step.twitch.question", TWITCH_CONNECT_TUTORIAL),
    SelectOption(
        get("modal.whitelist.step.twitch.question.yes"),
        OPTION_YES,
        emoji = "✅".toUnicodeEmoji()
    ),
    SelectOption(
        get("modal.whitelist.step.twitch.question.no"),
        OPTION_NO,
        emoji = "❌".toUnicodeEmoji()
    )
) {
    override fun InlineModal.buildModalComponents(interaction: StringSelectInteraction) {
    }

    override suspend fun verifyModalInput(event: ModalInteractionEvent) {
        if (OPTION_YES != selected) {
            throw ModalStepInputVerificationException(
                get("modal.whitelist.step.twitch.error.not-connected")
            )
        }
    }
}
