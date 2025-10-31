package dev.slne.surf.discord.interaction.modal.impl.ticket

import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.interaction.button.ButtonRegistry
import dev.slne.surf.discord.interaction.modal.DiscordModal
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.ticket.TicketData
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.ticket.TicketType
import dev.slne.surf.discord.util.Colors
import dev.slne.surf.discord.util.replyError
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import org.springframework.stereotype.Component

@Component
class EventSupportTicketModal(
    private val ticketService: TicketService
) : DiscordModal {
    override val id = "ticket:support:event"

    override fun create() = modal(id, translatable("ticket.support.event.modal.title")) {
        field {
            id = "issue"
            label = translatable("ticket.support.event.modal.field.issue.label")
            style = TextInputStyle.PARAGRAPH
            lengthRange = 10..4000
            placeholder =
                translatable("ticket.support.event.modal.field.issue.placeholder")
            required = true
        }
    }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val interaction = event.interaction
        val user = interaction.user

        val issue = interaction.getValue("issue")?.asString ?: return

        interaction.reply(translatable("ticket.creating")).setEphemeral(true).queue()

        val ticket =
            ticketService.createTicket(
                interaction.hook,
                TicketType.SUPPORT_EVENT,
                TicketData.of("issue" to issue)
            ) ?: run {
                if (ticketService.hasOpenTicket(user.idLong, TicketType.SUPPORT_EVENT)) {
                    interaction.hook.editOriginal(translatable("ticket.support.event.already_open"))
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

        interaction.hook.editOriginal(translatable("ticket.created", thread.asMention))
            .queue()

        thread.sendMessage(user.asMention).queue()
        thread.sendMessageEmbeds(
            embed {
                title = translatable("ticket.support.event.embed.title")
                description = translatable("ticket.support.event.embed.description")
                color = Colors.SUCCESS

                field {
                    name = translatable("ticket.support.event.embed.field.issue.name")
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