package dev.slne.discord.discord.interaction.modal.step.creator.report.steps

import dev.minn.jda.ktx.interactions.components.InlineModal
import dev.minn.jda.ktx.interactions.components.SelectOption
import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.ModalSelectionStep
import dev.slne.discord.discord.interaction.modal.step.StepBuilder
import dev.slne.discord.discord.interaction.modal.step.creator.report.steps.griefing.ReportTicketGriefingStep
import dev.slne.discord.discord.interaction.modal.step.creator.report.steps.player.ReportTicketPlayerStep
import dev.slne.discord.getBean
import dev.slne.discord.message.translatable
import dev.slne.discord.persistence.service.user.UserService
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

private const val OPTION_GRIEFING = "griefing"
private const val OPTION_PLAYER = "player"

private const val REPORTING_PLAYER_NAME_INPUT = "player-name"

class ReportTicketSelectTypeStep : ModalSelectionStep(
    translatable("modal.report.step.type.selection.title"),
    SelectOption(
        translatable("modal.report.step.type.selection.griefing.label"),
        OPTION_GRIEFING,
        description = translatable("modal.report.step.type.selection.griefing.description")
    ),
    SelectOption(
        translatable("modal.report.step.type.selection.player.label"),
        OPTION_PLAYER,
        description = translatable("modal.report.step.type.selection.player.description")
    )
) {
    private var playerName: String? = null

    override fun InlineModal.buildModalComponents(interaction: StringSelectInteraction) {
        val label = if (isGriefing) {
            translatable("modal.report.step.type.input.griefing.own-name")
        } else {
            translatable("modal.report.step.type.input.report.reported-player")
        }

        short(
            REPORTING_PLAYER_NAME_INPUT,
            label,
            required = true,
            requiredLength = 3..16,
            placeholder = "Notch"
        )
    }

    override suspend fun verifyModalInput(event: ModalInteractionEvent) {
        playerName = getInput(event, REPORTING_PLAYER_NAME_INPUT)
    }

    override fun MessageQueue.buildOpenMessages(thread: ThreadChannel) {
        addEmptyLine()
        addMessage(translatable("modal.report.step.type.messages.player-name", playerName))
    }

    override suspend fun onPostThreadCreated(thread: ThreadChannel) {
        val playerName = playerName

        if (playerName != null) {
            val uuid = getBean<UserService>().getUuidByUsername(playerName)
//
//            val whitelists = WhitelistService.checkWhitelists(uuid = uuid)
//
//            for (whitelist in whitelists) {
//                val embed = MessageManager.getWhitelistQueryEmbed(whitelist)
//                thread.sendMessageEmbeds(embed).queue()
//            }
        }
    }

    override fun buildChildSteps(): StepBuilder {
        return if (isGriefing) {
            StepBuilder.startWith(ReportTicketGriefingStep())
        } else if (isPlayer) {
            StepBuilder.startWith(ReportTicketPlayerStep())
        } else {
            StepBuilder.empty()
        }
    }

    private val isGriefing: Boolean
        get() = OPTION_GRIEFING == selected

    private val isPlayer: Boolean
        get() = OPTION_PLAYER == selected
}
