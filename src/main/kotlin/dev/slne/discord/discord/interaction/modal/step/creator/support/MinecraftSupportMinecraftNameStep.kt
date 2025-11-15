package dev.slne.discord.discord.interaction.modal.step.creator.support

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

private const val MINECRAFT_NAME = "minecraftName-753489785403534870"

class MinecraftSupportMinecraftNameStep : ModalStep() {
    private var minecraftName: String? = null

    override fun buildModalComponents(builder: Modal.Builder, interaction: StringSelectInteraction) {
        builder.addComponents(
            Label.of(
                translatable("modal.minecraft-support.minecraft-name.label"),
                TextInput.create(MINECRAFT_NAME, TextInputStyle.SHORT)
                    .setRequired(true)
                    .setRequiredRange(3, 16)
                    .build()
            )
        )
    }

    override suspend fun verifyModalInput(event: ModalInteractionEvent) {
        minecraftName = event[MINECRAFT_NAME]
    }

    override fun MessageQueue.buildOpenMessages(thread: ThreadChannel) {
        addEmptyLine()
        addMessage(translatable("modal.minecraft-support.minecraft-name.message", minecraftName))
    }
}
