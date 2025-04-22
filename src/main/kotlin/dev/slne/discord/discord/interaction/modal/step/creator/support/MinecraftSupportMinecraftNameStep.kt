package dev.slne.discord.discord.interaction.modal.step.creator.support

import dev.minn.jda.ktx.interactions.components.InlineModal
import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.ModalStep
import dev.slne.discord.message.translatable
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

private const val MINECRAFT_NAME = "minecraftName-753489785403534870"

class MinecraftSupportMinecraftNameStep : ModalStep() {
    private var minecraftName: String? = null

    override fun InlineModal.buildModalComponents(interaction: StringSelectInteraction) {
        short(
            MINECRAFT_NAME,
            translatable("modal.minecraft-support.minecraft-name.label"),
            required = true,
            requiredLength = 3..16
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
