package dev.slne.surf.discord.interaction.modal.impl.ticket.whitelist

import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.interaction.modal.DiscordModal
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.util.asTicketOrThrow
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.modals.Modal
import org.springframework.stereotype.Component

@Component
class WhitelistProceedModal(
    private val ticketService: TicketService
) : DiscordModal {
    override val id = "ticket:whitelist:proceed"

    override suspend fun create(hook: InteractionHook, vararg data: String): Modal {
        val ticket = hook.asTicketOrThrow()

        val minecraft = ticket.ticketData["minecraft"]
            ?: error("Minecraft username missing for ticket: ${ticket.ticketId}")
        val twitch = ticket.ticketData["twitch"]
            ?: error("Twitch username missing for ticket: ${ticket.ticketId}")

        return modal(id, translatable("ticket.whitelist.proceed.modal.title")) {
            textInput {
                id = "minecraft"
                label = translatable("ticket.whitelist.proceed.modal.field.minecraft.label")
                required = true
                style = TextInputStyle.SHORT
                value = minecraft
            }

            textInput {
                id = "twitch"
                label = translatable("ticket.whitelist.proceed.modal.field.twitch.label")
                required = true
                style = TextInputStyle.SHORT
                value = twitch
            }

            textInput {
                id = "discord-name"
                label = translatable("ticket.whitelist.proceed.modal.field.discord-name.label")
                required = true
                style = TextInputStyle.SHORT
                value = ticket.authorName
            }

            textInput {
                id = "discord-id"
                label = translatable("ticket.whitelist.proceed.modal.field.discord-id.label")
                required = true
                style = TextInputStyle.SHORT
                value = ticket.authorId.toString()
            }
        }
    }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        event.reply("Der Whitelist Antrag wurde eingereicht.").setEphemeral(true).queue()

        //TODO: Weiterverarbeitung - Database
    }
}