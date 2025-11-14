package dev.slne.surf.discord.interaction.modal.impl

import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.interaction.modal.DiscordModal
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.ticket.TicketService
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import org.springframework.stereotype.Component

@Component
class CustomCloseReasonModal(
    private val ticketService: TicketService
) : DiscordModal {
    override val id = "ticket:close:reason:custom"
    override fun create() = modal(id, translatable("ticket.close.reason.custom.modal.title")) {
        textInput {
            id = "custom-close-reason-content"
            label = translatable("ticket.close.reason.custom.modal.field.label")
            style = TextInputStyle.PARAGRAPH
            placeholder = translatable("ticket.close.reason.custom.modal.field.placeholder")
        }
    }


    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val interaction = event.interaction
        val customReason = interaction.getValue("custom-close-reason-content")?.asString ?: return

        interaction.hook.deleteOriginal()
        interaction.reply(translatable("ticket.closing")).setEphemeral(true).submit(true).await()

        ticketService.closeTicket(interaction.hook, customReason)
    }
}