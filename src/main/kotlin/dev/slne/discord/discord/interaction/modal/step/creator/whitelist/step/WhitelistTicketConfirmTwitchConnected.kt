package dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step

import dev.slne.discord.discord.interaction.modal.step.ModalComponentBuilder
import dev.slne.discord.discord.interaction.modal.step.ModalSelectionStep
import dev.slne.discord.message.RawMessages.Companion.get
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.SelectOption

class WhitelistTicketConfirmTwitchConnected : ModalSelectionStep(
    get("modal.whitelist.step.twitch.question", TWITCH_CONNECT_TUTORIAL),
    SelectOption.of(
        get("modal.whitelist.step.twitch.question.yes")!!,
        OPTION_YES
    ).withEmoji(Emoji.fromUnicode("✅")),
    SelectOption.of(
        get("modal.whitelist.step.twitch.question.no")!!,
        OPTION_NO
    ).withEmoji(Emoji.fromUnicode("❌"))
) {
    override fun buildModalComponents(builder: ModalComponentBuilder) {
    }

    @Throws(ModalStepInputVerificationException::class)
    override fun verifyModalInput(event: ModalInteractionEvent) {
        if (getSelected().equals(OPTION_NO)) {
            throw ModalStepInputVerificationException(
                get("modal.whitelist.step.twitch.error.not-connected")
            )
        }
    }

    companion object {
        private const val TWITCH_CONNECT_TUTORIAL =
            "https://server.castcrafter.de/support.html#link-twitch"

        private const val OPTION_YES = "yes"
        private const val OPTION_NO = "no"
    }
}
