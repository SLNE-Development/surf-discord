package dev.slne.surf.discord.interaction.modal.impl

import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.interaction.modal.DiscordModal
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.util.asTicketOrThrow
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal
import org.springframework.stereotype.Component

@Component
class WhitelistProceedModal(
    private val ticketService: TicketService
) : DiscordModal {
    override val id = "ticket:whitelist:proceed"
    override fun create() = error("Unsupported")

    override suspend fun create(hook: InteractionHook): Modal {
        val ticket = hook.asTicketOrThrow()

        val minecraft = ticket.ticketData?.let {
            it.split(":")[1]
        }

        val twitch = ticket.ticketData?.let {
            it.split(":")[2]
        }

        return modal(id, "Spieler whitelisten") {
            field {
                id = "minecraft"
                label = "Minecraft Benutzername"
                required = true
                style = TextInputStyle.SHORT
                value = minecraft
            }

            field {
                id = "twitch"
                label = "Twitch Benutzername"
                required = true
                style = TextInputStyle.SHORT
                value = twitch
            }

            field {
                id = "discord-name"
                label = "Discord Benutzername"
                required = true
                style = TextInputStyle.SHORT
                value = ticket.authorName
            }

            field {
                id = "discord-id"
                label = "Discord ID"
                required = true
                style = TextInputStyle.SHORT
                value = ticket.authorId.toString()
            }
        }
    }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        event.reply("Der Whitelist Antrag wurde eingereicht.").setEphemeral(true).queue()

        ticketService.closeTicket(event.hook, "Du befindest dich nun auf der Whitelist.")

        //TODO: Weiterverarbeitung - Database
    }
}