package dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step

import dev.slne.data.api.DataApi
import dev.slne.discord.Bootstrap
import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import java.util.*
import java.util.concurrent.CompletionException

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

    @Throws(ModalStepInputVerificationException::class)
    override fun verifyModalInput(event: ModalInteractionEvent) {
        this.minecraftName = getRequiredInput(event, MINECRAFT_NAME)
        val uuid: UUID

        try {
            uuid = DataApi.getUuidByPlayerName(this.minecraftName).join()
        } catch (e: CompletionException) {
            throw ModalStepInputVerificationException(
                RawMessages.get("modal.whitelist.step.minecraft.invalid"), e
            )
        }

        if (WHITELIST_SERVICE.get().isWhitelisted(uuid, null, null).join()) {
            throw ModalStepInputVerificationException(
                RawMessages.get("interaction.command.ticket.whitelist.already-whitelisted")
            )
        }
    }

    override fun buildOpenMessages(messages: MessageQueue, channel: TextChannel?) {
        messages.addMessage(
            RawMessages.get(
                "modal.whitelist.step.minecraft.open",
                this.minecraftName
            )
        )
    }

    companion object {
        private val WHITELIST_SERVICE: Lazy<WhitelistService> = Lazy.of {
            Bootstrap.getContext().getBean(
                WhitelistService::class.java
            )
        }
        private const val MINECRAFT_NAME = "minecraft-name"
    }
}
