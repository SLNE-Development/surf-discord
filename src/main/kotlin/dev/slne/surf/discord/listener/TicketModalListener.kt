package dev.slne.surf.discord.listener

import dev.slne.surf.discord.command.dsl.embed
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.ticket.TicketType
import dev.slne.surf.discord.util.absoluteDiscordTimeStamp
import dev.slne.surf.discord.util.relativeDiscordTimeStamp
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.springframework.stereotype.Component
import java.awt.Color

@Component
class TicketModalListener(
    private val jda: JDA,
    private val ticketService: TicketService,
    private val ticketScope: CoroutineScope
) : ListenerAdapter() {

    @PostConstruct
    fun init() {
        jda.addEventListener(this)
    }

    override fun onModalInteraction(event: ModalInteractionEvent) {
        when (event.modalId) {
            "modal-whitelist" -> handleWhitelistTicketCreation(event)
            "custom-close-reason" -> handleTicketCustomReasonClose(event)
        }
    }

    private fun handleTicketCustomReasonClose(interaction: ModalInteractionEvent) {
        val customReason = interaction.getValue("custom-close-reason-content")?.asString ?: return

        ticketScope.launch {
            ticketService.closeTicket(interaction.hook, customReason)
            interaction.reply("Das Ticket wird geschlossen...").setEphemeral(true).queue()
        }
    }

    private fun handleWhitelistTicketCreation(interaction: ModalInteractionEvent) {
        val whitelistName = interaction.getValue("whitelist-name")?.asString ?: return
        val whitelistTwitch = interaction.getValue("whitelist-twitch")?.asString ?: return

        interaction.reply("Das Ticket wird erstellt...").setEphemeral(true).queue()

        ticketScope.launch {
            val user = interaction.user
            val ticket =
                ticketService.createTicket(interaction.hook, TicketType.WHITELIST) ?: run {
                    val activeTicket =
                        ticketService.getTicketByUserAndType(
                            interaction.idLong,
                            TicketType.WHITELIST
                        )

                    if (activeTicket != null) {
                        interaction.reply("Du hast bereits ein offenes Whitelist Ticket.")
                            .setEphemeral(true).queue()
                    } else {
                        interaction.reply("Dein Ticket konnte nicht erstellt werden. Bitte versuche es später erneut.")
                            .setEphemeral(true).queue()
                    }
                    return@launch
                }

            ticketService.updateData(ticket, "whitelist:$whitelistName:$whitelistTwitch")

            val thread = ticket.getThreadChannel() ?: run {
                interaction
                    .reply("Dein Ticket konnte nicht erstellt werden.")
                    .setEphemeral(true)
                    .queue()
                return@launch
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

                    footer =
                        "Erstellt von ${interaction.user.name} ${ticket.createdAt.relativeDiscordTimeStamp()} (${ticket.createdAt.absoluteDiscordTimeStamp()})"
                }
            ).addActionRow(
                Button.success("whitelist-complete", "Whitelist Annehmen"),
                Button.danger("close-ticket", "Ticket Schließen")
            )
                .queue()
        }
    }
}