package dev.slne.discordold.discord.interaction.modal.step.creator.whitelist.step

import dev.minn.jda.ktx.interactions.components.InlineModal
import dev.slne.discordold.discord.interaction.modal.step.MessageQueue
import dev.slne.discordold.discord.interaction.modal.step.ModalStep
import dev.slne.discordold.message.translatable
import dev.slne.discordold.persistence.service.user.UserService
import dev.slne.discordold.persistence.service.whitelist.WhitelistService
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

private const val MINECRAFT_NAME = "minecraft-name"

class WhitelistTicketMinecraftNameStep(
    parent: ModalStep,
    private val userService: UserService,
    private val whitelistService: WhitelistService
) : ModalStep() {

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

        val uuid = userService.getUuidByUsername(minecraftName!!)
            ?: throw ModalStepInputVerificationException(
                translatable("modal.whitelist.step.minecraft.invalid")
            )

        if (whitelistService.isWhitelisted(uuid, null, null)) {
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
