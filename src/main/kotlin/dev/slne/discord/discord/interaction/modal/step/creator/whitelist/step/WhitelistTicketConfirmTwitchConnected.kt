package dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step

import dev.slne.discord.discord.interaction.modal.step.ModalSelectionStep
import dev.slne.discord.exception.step.modal.selection.ValidateModalSelectionException
import dev.slne.discord.message.translatable
import net.dv8tion.jda.api.components.selections.SelectOption
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction
import net.dv8tion.jda.api.modals.Modal

private const val TWITCH_CONNECT_TUTORIAL = "https://server.castcrafter.de/support.html#link-twitch"
private const val OPTION_YES = "yes"
private const val OPTION_NO = "no"

class WhitelistTicketConfirmTwitchConnected : ModalSelectionStep(
    translatable("modal.whitelist.step.twitch.question", TWITCH_CONNECT_TUTORIAL),
    SelectOption.of(
        translatable("modal.whitelist.step.twitch.question.yes"),
        OPTION_YES
    ).withEmoji(Emoji.fromUnicode("✅")),
    SelectOption.of(
        translatable("modal.whitelist.step.twitch.question.no"),
        OPTION_NO
    ).withEmoji(Emoji.fromUnicode("❌"))
) {
    override fun buildModalComponents(builder: Modal.Builder, interaction: StringSelectInteraction) {
    }

    override suspend fun afterSelection(event: StringSelectInteractionEvent) {
        if (OPTION_YES != selected) {
            throw ValidateModalSelectionException(
                translatable("modal.whitelist.step.twitch.error.not-connected")
            )
        }
    }
}
