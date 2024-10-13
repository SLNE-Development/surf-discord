package dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step

import dev.minn.jda.ktx.interactions.components.InlineModal
import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.ModalStep
import dev.slne.discord.message.RawMessages
import dev.slne.discord.spring.service.user.UserService
import dev.slne.discord.spring.service.whitelist.WhitelistService
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent

private const val MINECRAFT_NAME = "minecraft-name"

class WhitelistTicketMinecraftNameStep(parent: ModalStep) : ModalStep() {

    private var minecraftName: String? = null

    override fun InlineModal.buildModalComponents() {
        short(
            MINECRAFT_NAME,
            RawMessages.get("modal.whitelist.step.minecraft.label"),
            required = true,
            requiredLength = 3..16
        )
    }

    override suspend fun verifyModalInput(event: ModalInteractionEvent) {
        minecraftName = getInput(event, MINECRAFT_NAME)

        val uuid = UserService.getUuidByUsername(minecraftName!!)
            ?: throw ModalStepInputVerificationException(
                RawMessages.get("modal.whitelist.step.minecraft.invalid")
            )

        if (WhitelistService.isWhitelisted(uuid, null, null)) {
            throw ModalStepInputVerificationException(
                RawMessages.get("interaction.command.ticket.whitelist.already-whitelisted")
            )
        }
    }

    override fun MessageQueue.buildOpenMessages(thread: ThreadChannel) {
        addMessage(
            RawMessages.get(
                "modal.whitelist.step.minecraft.open",
                minecraftName
            )
        )
    }
}
