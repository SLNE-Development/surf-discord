package dev.slne.surf.discord.interaction.modal.impl

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
class BugreportTicketModal(
    private val ticketService: TicketService
) : DiscordModal {
    override val id = "ticket:bugreport"

    override fun create() = modal(id, "BugReport Ticket") {
        field {
            id = "issue"
            label = "Beschreibe den Fehler"
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

        val issue = interaction.getValue("issue")?.asString ?: return

        interaction.reply("Das Ticket wird erstellt...").setEphemeral(true).queue()

        val ticket =
            ticketService.createTicket(
                interaction.hook,
                TicketType.BUGREPORT,
                TicketData.of("issue" to issue)
            ) ?: run {
                if (ticketService.hasOpenTicket(user.idLong, TicketType.BUGREPORT)) {
                    interaction.hook.editOriginal("Du hast bereits ein offenes BugReport Ticket.")
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
                    "Dein BugReport Ticket wurde erstellt und wir haben deine Informationen erhalten. Bitte habe ein wenig Geduld, während das Team dein Anliegen bearbeitet."
                color = Colors.SUCCESS

                field {
                    name = "Gefundener Bug"
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