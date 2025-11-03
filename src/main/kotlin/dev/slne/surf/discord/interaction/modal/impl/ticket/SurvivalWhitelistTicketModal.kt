package dev.slne.surf.discord.interaction.modal.impl.ticket

import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.interaction.modal.DiscordModal
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.ticket.TicketService
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import org.springframework.stereotype.Component

@Component
class SurvivalWhitelistTicketModal(
    private val ticketService: TicketService
) : DiscordModal {
    override val id = "ticket:whitelist_survival"
    override fun create() = modal(id, translatable("ticket.whitelist.survival.title")) {
        textInput {
            id = "minecraft"
            label = translatable("ticket.whitelist.survival.field.name")
            style = TextInputStyle.SHORT
            placeholder = "CastCrafter"
            required = true
            lengthRange = 3..16
        }
    }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val interaction = event.interaction
        val user = interaction.user

        val whitelistName = interaction.getValue("minecraft")?.asString ?: return

        interaction.reply(translatable("ticket.whitelist.survival.processing")).setEphemeral(true)
            .queue()

    }
}