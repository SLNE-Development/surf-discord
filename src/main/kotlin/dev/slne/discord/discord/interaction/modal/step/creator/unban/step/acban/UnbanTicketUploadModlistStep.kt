package dev.slne.discord.discord.interaction.modal.step.creator.unban.step.acban

import dev.minn.jda.ktx.interactions.components.InlineModal
import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.ModalStep
import dev.slne.discord.message.translatable
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

private const val MODLIST = "modlist"

class UnbanTicketUploadModlistStep : ModalStep() {
    private var modlist: String? = null

    override fun InlineModal.buildModalComponents(
        interaction: StringSelectInteraction
    ) {
        paragraph(
            MODLIST,
            translatable("modal.unban.step.modlist.input.label"),
            required = true,
            placeholder = translatable("modal.unban.step.modlist.input.placeholder")
        )
    }

    override suspend fun verifyModalInput(event: ModalInteractionEvent) {
        modlist = getInput(event, MODLIST)
    }

    override fun MessageQueue.buildOpenMessages(thread: ThreadChannel) {
        val modlist = modlist

        if (modlist != null) {
            addEmptyLine()
            addEmptyLine()
            addMessage(
                translatable(
                    "modal.unban.step.modlist.messages.open",
                    modlist.lines().joinToString { if (it.isNotBlank()) "`$it`" else "" }
                )
            )
        }
    }
}