package dev.slne.surf.discord.interaction.modal.impl.ticket

import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.interaction.button.ButtonRegistry
import dev.slne.surf.discord.interaction.modal.DiscordModal
import dev.slne.surf.discord.ticket.TicketData
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.ticket.TicketType
import dev.slne.surf.discord.util.Colors
import dev.slne.surf.discord.util.replyError
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import org.springframework.stereotype.Component

@Component
class ReportTicketModal(
    private val ticketService: TicketService
) : DiscordModal {
    override val id = "ticket:report"

    override fun create() = modal(id, "Report Ticket") {
        field {
            id = "target"
            label = "Name des Spielers, den du melden möchtest"
            style = TextInputStyle.SHORT
            placeholder = "Bitte gib den genauen Minecraft Benutzernamen an."
            required = true
            lengthRange = 3..16
        }
        field {
            id = "issue"
            label = "Beschreibe dein Anliegen"
            style = TextInputStyle.PARAGRAPH
            lengthRange = 10..4000
            placeholder =
                "Bitte gib so viele Informationen wie möglich an, damit wir dir bestmöglich helfen können."
            required = true
        }
    }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val interaction = event.interaction
        val user = interaction.user

        val target = interaction.getValue("target")?.asString ?: return
        val issue = interaction.getValue("issue")?.asString ?: return

        interaction.reply("Das Ticket wird erstellt...").setEphemeral(true).queue()

        val ticket =
            ticketService.createTicket(
                interaction.hook,
                TicketType.REPORT,
                TicketData.of("target" to target, "issue" to issue)
            ) ?: run {
                if (ticketService.hasOpenTicket(user.idLong, TicketType.REPORT)) {
                    interaction.hook.editOriginal("Du hast bereits ein offenes Report Ticket.")
                        .queue()
                } else {
                    interaction.hook.replyError()
                }
                return
            }

        val thread = ticket.getThreadChannel() ?: run {
            interaction.hook.replyError()
            return
        }

        interaction.hook.editOriginal("Dein Ticket wurde erstellt: ${thread.asMention}")
            .queue()

        thread.sendMessage(user.asMention).queue()
        thread.sendMessageEmbeds(
            embed {
                title = "Willkommen im Ticket!"
                description =
                    "Dein Report Ticket wurde erstellt und wir haben deine Informationen erhalten. Bitte habe ein wenig Geduld, während das Team dein Anliegen bearbeitet."
                color = Colors.SUCCESS

                field {
                    name = "Gemeldeter Spieler"
                    value = target
                    inline = true
                }

                field {
                    name = "Anliegen"
                    value = issue
                    inline = true
                }//TODO: Add Whitelist Information
            }
        ).addActionRow(
            getBean<ButtonRegistry>().get("ticket:close").button, //TODO: Add Laby.Net Profile Button
            getBean<ButtonRegistry>().get("ticket:claim").button
        ).queue()
    }
}