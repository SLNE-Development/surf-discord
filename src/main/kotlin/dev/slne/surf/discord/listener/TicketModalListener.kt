package dev.slne.surf.discord.listener

import dev.slne.surf.discord.command.dsl.embed
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.ticket.TicketType
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
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
        }
    }

    private fun handleWhitelistTicketCreation(interaction: ModalInteractionEvent) {
        val whitelistName = interaction.getValue("whitelist-name")?.asString ?: return
        val whitelistTwitch = interaction.getValue("whitelist-twitch")?.asString ?: return

        interaction.hook.editOriginal("Das Ticket wird erstellt...").queue()

        ticketScope.launch {
            val ticket =
                ticketService.createTicket(interaction.hook, TicketType.WHITELIST) ?: run {
                    val activeTicket =
                        ticketService.getTicket(interaction.idLong, TicketType.WHITELIST)

                    if (activeTicket != null) {
                        interaction.hook.editOriginalEmbeds(
                            embed {
                                title = "Ticket Erstellung Fehlgeschlagen"
                                description =
                                    "Du hast bereits ein offenes Whitelist Ticket: "
                                color = Color.RED
                            }
                        ).queue()
                    } else {
                        interaction.hook.editOriginalEmbeds(
                            embed {
                                title = "Ticket Erstellung Fehlgeschlagen"
                                description =
                                    "Es ist ein unbekannter Fehler aufgetreten. Bitte versuche es später erneut. Sollte dieses Problem weiterhin bestehen, kontaktiere ein Teammitglied."
                                color = Color.RED
                            }
                        ).queue()
                    }
                    return@launch
                }

            ticket.getThreadChannel()?.sendMessageEmbeds(
                embed {
                    title = "Whitelist Ticket"
                    description =
                        "Dein Whitelist Ticket wurde erstellt. Bitte habe etwas Geduld, bis sich ein Teammitglied um deinen Antrag kümmert."
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
            )?.queue()
        }
    }
}