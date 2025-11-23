package dev.slne.surf.discord.interaction.modal.impl.ticket.whitelist

import dev.slne.surf.discord.DiscordBot
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.interaction.button.ButtonRegistry
import dev.slne.surf.discord.interaction.modal.DiscordModal
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.ticket.TicketService
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import org.springframework.stereotype.Component

@Component
class WhitelistTicketModal(
    private val ticketService: TicketService,
) : DiscordModal {
    private val buttonRegistry by lazy {
        getBean<ButtonRegistry>()
    }
    override val id = "ticket:whitelist"
    override fun create() = modal(id, translatable("ticket.whitelist.title")) {
        textInput {
            id = "whitelist-name"
            label = translatable("ticket.whitelist.field.name")
            style = TextInputStyle.SHORT
            placeholder = "CastCrafter"
            required = true
            lengthRange = 3..16
        }
    }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val interaction = event.interaction
        val user = interaction.user
        val whitelistName = interaction.getValue("whitelist-name")?.asString ?: return

        if (!DiscordBot.SURVIVAL_ENABLED) {
            interaction.replyEmbeds(embed {
                title = "Aktuell können keine Whitelist Tickets erstellt werden."
                description =
                    "Aufgrund der aktuellen Wartungsarbeiten am Survival Server können keine Whitelist Tickets erstellt werden."
            }).setEphemeral(true).queue()
            return
        }

        interaction.reply(translatable("ticket.whitelist.processing")).setEphemeral(true).queue()

        //TODO: Whitelist player & link to discord account
    }
}