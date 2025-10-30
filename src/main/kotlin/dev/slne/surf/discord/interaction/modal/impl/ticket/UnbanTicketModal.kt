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
class UnbanTicketModal(
    private val ticketService: TicketService
) : DiscordModal {
    override val id = "ticket:unban"

    override fun create() = modal(id, "Entbannungsantrag") {
        field {
            id = "punish-id"
            label = "Punishment ID"
            style = TextInputStyle.SHORT
            placeholder = "Gib hier die Punishment ID deines Banns ein."
            required = true
        }
        field {
            id = "issue"
            label = "Warum wurdest du gebannt?"
            style = TextInputStyle.PARAGRAPH
            lengthRange = 50..4000
            placeholder =
                "Ich habe garnichts gemacht!"
            required = true
        }

        field {
            id = "reason"
            label = "Warum sollten wir dich entbannen?"
            style = TextInputStyle.PARAGRAPH
            lengthRange = 100..4000
            placeholder =
                "Ich möchte bitte wieder spielen bitte bitte"
            required = true
        }
    }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val interaction = event.interaction
        val user = interaction.user

        val punishId = interaction.getValue("punish-id")?.asString ?: return
        val issue = interaction.getValue("issue")?.asString ?: return
        val reason = interaction.getValue("reason")?.asString ?: return

        interaction.reply("Das Ticket wird erstellt...").setEphemeral(true).queue()

        val ticket =
            ticketService.createTicket(
                interaction.hook,
                TicketType.UNBAN,
                TicketData.of("punish-id" to punishId, "reason" to reason, "issue" to issue)
            ) ?: run {
                if (ticketService.hasOpenTicket(user.idLong, TicketType.UNBAN)) {
                    interaction.hook.editOriginal("Du hast bereits einen offenen Entbannungsantrag.")
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
                    "Dein Entbannungsantrag wurde erstellt und wir haben deine Informationen erhalten. Bitte habe ein wenig Geduld, während das Team dein Anliegen bearbeitet."
                color = Colors.SUCCESS

                field {
                    name = "Punishment ID"
                    value = punishId
                    inline = true
                }

                field {
                    name = "Banngrund"
                    value = issue
                    inline = true
                }

                field {
                    name = "Entbannungsgrund"
                    value = reason
                    inline = true
                }//TODO: Add Whitelist Information
            }
        ).addActionRow(
            getBean<ButtonRegistry>().get("ticket:close").button, //TODO: Add Laby.Net Profile Button
            getBean<ButtonRegistry>().get("ticket:claim").button
        ).queue()
    }
}