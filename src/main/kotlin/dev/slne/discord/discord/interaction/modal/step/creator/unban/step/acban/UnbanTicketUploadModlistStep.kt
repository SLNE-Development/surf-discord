package dev.slne.discord.discord.interaction.modal.step.creator.unban.step.acban

import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.ModalStep
import dev.slne.discord.message.translatable
import net.dv8tion.jda.api.components.label.Label
import net.dv8tion.jda.api.components.textinput.TextInput
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction
import net.dv8tion.jda.api.modals.Modal

private const val MODLIST = "modlist"

class UnbanTicketUploadModlistStep : ModalStep() {
    private var modlist: String? = null


    override fun buildModalComponents(builder: Modal.Builder, interaction: StringSelectInteraction) {
        builder.addComponents(
            Label.of(
                translatable("modal.unban.step.modlist.input.label"),
                TextInput.create(MODLIST, TextInputStyle.PARAGRAPH)
                    .setPlaceholder(translatable("modal.unban.step.modlist.input.placeholder"))
                    .setRequired(true)
                    .build()
            )
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