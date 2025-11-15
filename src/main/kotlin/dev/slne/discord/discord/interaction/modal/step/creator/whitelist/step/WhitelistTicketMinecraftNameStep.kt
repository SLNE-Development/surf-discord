package dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step

import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.ModalStep
import dev.slne.discord.message.translatable
import dev.slne.discord.persistence.service.user.UserService
import dev.slne.discord.persistence.service.whitelist.WhitelistService
import net.dv8tion.jda.api.components.label.Label
import net.dv8tion.jda.api.components.textinput.TextInput
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction
import net.dv8tion.jda.api.modals.Modal

private const val MINECRAFT_NAME = "minecraft-name"

class WhitelistTicketMinecraftNameStep(
    parent: ModalStep,
    private val userService: UserService,
    private val whitelistService: WhitelistService
) : ModalStep() {

    private var minecraftName: String? = null

    override fun buildModalComponents(builder: Modal.Builder, interaction: StringSelectInteraction) {
        builder.addComponents(
            Label.of(
                translatable("modal.whitelist.step.minecraft.label"),
                TextInput.create(MINECRAFT_NAME, TextInputStyle.SHORT)
                    .setRequired(true)
                    .setRequiredRange(3, 16)
                    .build()
            )
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
