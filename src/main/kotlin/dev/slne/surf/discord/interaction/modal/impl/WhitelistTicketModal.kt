package dev.slne.surf.discord.interaction.modal.impl

import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.interaction.button.ButtonRegistry
import dev.slne.surf.discord.interaction.modal.DiscordModal
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.ticket.TicketType
import dev.slne.surf.discord.util.replyError
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import org.springframework.stereotype.Component
import java.awt.Color

@Component
class WhitelistTicketModal(
    private val ticketService: TicketService
) : DiscordModal {
    override val id = "ticket:whitelist"
    override val modal = modal(id, "Whitelist Anfrage") {
        field {
            id = "whitelist-name"
            label = "Minecraft Name"
            style = TextInputStyle.SHORT
            placeholder = "CastCrafter"
            required = true
            lengthRange = 3..16
        }

        field {
            id = "whitelist-twitch"
            label = "Twitch Name"
            style = TextInputStyle.SHORT
            placeholder = "CastCrafter"
            required = true
        }
    }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val interaction = event.interaction
        val user = interaction.user

        val whitelistName = interaction.getValue("whitelist-name")?.asString ?: return
        val whitelistTwitch = interaction.getValue("whitelist-twitch")?.asString ?: return

        interaction.reply("Das Ticket wird erstellt...").setEphemeral(true).queue()

        val ticket = ticketService.createTicket(interaction.hook, TicketType.WHITELIST) ?: run {
            if (ticketService.hasOpenTicket(user.idLong, TicketType.WHITELIST)) {
                interaction.hook.editOriginal("Du hast bereits ein offenes Whitelist Ticket.")
                    .queue()
            } else {
                interaction.hook.replyError()
            }
            return
        }

        ticketService.updateData(ticket, "whitelist:$whitelistName:$whitelistTwitch")

        val thread = ticket.getThreadChannel() ?: run {
            interaction.hook.replyError()
            return
        }

        interaction.hook.editOriginal("Dein Whitelist Ticket wurde erstellt: ${thread.asMention}")
            .queue()

        thread.sendMessage(user.asMention).queue()
        thread.sendMessageEmbeds(
            embed {
                title = "Willkommen im Whitelist Ticket!"
                description =
                    "Dein Whitelist Ticket wurde erstellt und wir haben deine Informationen erhalten. Bitte habe ein wenig Geduld, während das Team deine Anfrage bearbeitet."
                color = Color.YELLOW

                field {
                    name = "Whitelist Name"
                    value = whitelistName
                    inline = true
                }

                field {
                    name = "Twitch Name"
                    value = whitelistTwitch
                    inline = true
                }

                field {
                    name = "Discord Name"
                    value = interaction.user.name
                    inline = true
                }

                field {
                    name = "Discord ID"
                    value = interaction.user.id
                    inline = true
                }
            }
        ).addActionRow(
            Button.of(
                ButtonStyle.SECONDARY,
                "whitelist:complete",
                "Whitelist annehmen",
                Emoji.fromCustom("checkmark", 1433072075446423754, false)
            ),
            getBean<ButtonRegistry>().get("ticket:close").button,
            Button.link("https://twitch.tv/$whitelistTwitch", "Twitch"),
            Button.link("https://www.laby.net/$whitelistName", "Minecraft"),
        ).queue()
    }
}