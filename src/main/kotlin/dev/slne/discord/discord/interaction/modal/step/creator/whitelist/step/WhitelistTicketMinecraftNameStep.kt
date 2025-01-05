package dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step

import dev.minn.jda.ktx.interactions.components.InlineModal
import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.ModalStep
import dev.slne.discord.message.translatable
import dev.slne.discord.persistence.service.user.UserService
import dev.slne.discord.persistence.service.whitelist.WhitelistRepository
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

private const val MINECRAFT_NAME = "minecraft-name"

class WhitelistTicketMinecraftNameStep(parent: ModalStep) : ModalStep() {

    private var minecraftName: String? = null

    override fun InlineModal.buildModalComponents(interaction: StringSelectInteraction) {
        short(
            MINECRAFT_NAME,
            translatable("modal.whitelist.step.minecraft.label"),
            required = true,
            requiredLength = 3..16
        )
    }

    override suspend fun verifyModalInput(event: ModalInteractionEvent) {
        minecraftName = getInput(event, MINECRAFT_NAME)

        val uuid = UserService.getUuidByUsername(minecraftName!!)
            ?: throw ModalStepInputVerificationException(
                translatable("modal.whitelist.step.minecraft.invalid")
            )

        if (WhitelistRepository.isWhitelisted(uuid, null, null)) {
            throw ModalStepInputVerificationException(
                translatable("interaction.command.ticket.whitelist.already-whitelisted")
            )
        }
    }

    override fun MessageQueue.buildOpenMessages(thread: ThreadChannel) {
        addMessage(
            translatable(
                "modal.whitelist.step.minecraft.open",
                minecraftName
            )
        )
    }
}
