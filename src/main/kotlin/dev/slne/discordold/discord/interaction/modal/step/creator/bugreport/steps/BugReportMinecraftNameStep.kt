package dev.slne.discordold.discord.interaction.modal.step.creator.bugreport.steps

import dev.minn.jda.ktx.interactions.components.InlineModal
import dev.slne.discordold.discord.interaction.modal.step.MessageQueue
import dev.slne.discordold.discord.interaction.modal.step.ModalStep
import dev.slne.discordold.message.translatable
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

private const val MINECRAFT_NAME = "minecraft-name"

class BugReportMinecraftNameStep : ModalStep() {
    private var minecraftName: String? = null

    override fun InlineModal.buildModalComponents(interaction: StringSelectInteraction) {
        short(
            MINECRAFT_NAME,
            translatable("modal.bug-report.step.minecraft.label"),
            required = true,
            requiredLength = 3..16
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