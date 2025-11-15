package dev.slne.discord.discord.interaction.modal.step.creator.bugreport.steps

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

private const val MINECRAFT_NAME = "minecraft-name"

class BugReportMinecraftNameStep : ModalStep() {
    private var minecraftName: String? = null

    override fun buildModalComponents(builder: Modal.Builder, interaction: StringSelectInteraction) {
        builder.addComponents(
            Label.of(
                translatable("modal.bug-report.step.minecraft.label"),
                TextInput.create(MINECRAFT_NAME, TextInputStyle.SHORT)
                    .setRequiredRange(3, 16)
                    .setRequired(true)
                    .build()
            )
        )
    }

    override fun MessageQueue.buildOpenMessages(thread: ThreadChannel) {
        addEmptyLine()
        addMessage(
            translatable(
                "modal.bug-report.step.minecraft.messages.minecraft-name",
                minecraftName
            )
        )
    }

    override suspend fun verifyModalInput(event: ModalInteractionEvent) {
        minecraftName = event[MINECRAFT_NAME]
    }
}