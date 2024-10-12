package dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step

import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.ModalComponentBuilder
import dev.slne.discord.discord.interaction.modal.step.ModalStep
import dev.slne.discord.message.RawMessages
import dev.slne.discord.spring.service.user.UserService
import dev.slne.discord.spring.service.whitelist.WhitelistService
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle

private const val MINECRAFT_NAME = "minecraft-name"

class WhitelistTicketMinecraftNameStep(parent: ModalStep?) : ModalStep() {

    private var minecraftName: String? = null

    override fun buildModalComponents(builder: ModalComponentBuilder) {
        builder.addComponent(
            TextInput.create(
                MINECRAFT_NAME,
                RawMessages.get("modal.whitelist.step.minecraft.label"),
                TextInputStyle.SHORT
            )
                .setRequired(true)
                .setRequiredRange(3, 16)
                .build()
        )
    }

    override suspend fun verifyModalInput(event: ModalInteractionEvent) {
        minecraftName = getRequiredInput(event, MINECRAFT_NAME)
        
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

    override fun buildOpenMessages(messages: MessageQueue, thread: ThreadChannel) {
        messages.addMessage(
            RawMessages.get(
                "modal.whitelist.step.minecraft.open",
                minecraftName
            )
        )
    }
}
